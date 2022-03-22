package utils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.sound.midi.Soundbank;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.lang.reflect.Field;
import java.util.Map;

import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.objects.NativeObject;

public class JsonUtil {

    //基础数据类型
    private static final Set<String> BASE_CLASS_TYPE = new HashSet<>(Arrays.asList("String", "Integer", "Double", "Long", "Boolean"));
    //集合框架接口
    private static final Set<String> COLLECT_CLASS_TYPE = new HashSet<>(Arrays.asList("java.util.List", "java.util.Map"));
    //来自jdk8的ScriptEngine，js脚本处理引擎
    private static final ScriptEngine Engine = new ScriptEngineManager().getEngineByName("nashorn");




    public static <T> T resolver(String jsonStr, TypeReference<T> typeReference)
            throws ScriptException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        jsonStr = jsonStr.replace("\n", "").replace("\t", "");
        Object obj = Engine.eval("JSON.parse('" + jsonStr + "')");
        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror)obj;
        return parse(scriptObjectMirror, typeReference);
    }

    static <T> T parse(ScriptObjectMirror scriptObjectMirror, TypeReference<T> typeReference)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class classZz = Class.forName(typeReference.getTypeName());
        //如果泛型继承自集合，就走集合处理方式
        for (Class c : classZz.getInterfaces()) {
            if (COLLECT_CLASS_TYPE.contains(c.getTypeName())) {
                return (T)collectionParse(scriptObjectMirror,typeReference);
            }
        }
        //有可能泛型直接就写接口，走集合处理方式
        if (COLLECT_CLASS_TYPE.contains(typeReference.getTypeName())) {
            return (T)collectionParse(scriptObjectMirror,typeReference);
        }
        //反射获取一个对象
        Object object = classZz.newInstance();
        //遍历读取到的数据
        for (String key : scriptObjectMirror.keySet()) {
            Object value = scriptObjectMirror.get(key);
            //基础数据类型通过反射直接set
            if (BASE_CLASS_TYPE.contains(value.getClass().getSimpleName())) {
                setValue(object, key, value);
            } else if ("ScriptObjectMirror".contains(value.getClass().getSimpleName())) {
                //深度属性
                Class classZz1 = getValueClass(object, key);
                if (classZz1 != null) {
                    TypeReference typeReference1 = new TypeReference(){};
                    typeReference1.setTypeName(classZz1.getTypeName());
                    //递归，属性可能不是基础数据类型
                    setValue(object, key, parse((ScriptObjectMirror)value, typeReference1));
                }
            }else{
                //基础数据类型不知道有没有写全，反正除了ScriptObjectMirror对象，直接塞给他
                setValue(object, key, value);
            }
        }
        return (T)object;
    }

    /**
     * 获取object中filedName属性的类，不存在属性filedName则返回null
     * @param object
     * @param filedName
     * @return
     */
    private static Class getValueClass(Object object, String filedName) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(filedName)) {
                return field.getType();
            }
        }
        return null;
    }

    /**
     * 将value赋值给obj的filedName属性
     * @param object
     * @param filedName
     * @param value
     */
    private static void setValue(Object object, String filedName, Object value) {
        String methodName = "set" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1);
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, value.getClass());
            method.invoke(object, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 处理map和list这种直接的数据结构
     * @param scriptObjectMirror
     * @return
     */
    private static Object collectionParse(ScriptObjectMirror scriptObjectMirror ,TypeReference typeReference)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        //在子泛型的时候可能会出现null，这个时候用默认集合逻辑
        if(typeReference != null){
            Class classZz = Class.forName(typeReference.getTypeName());
            //如果泛型进来和集合没关系，就走普通类型
            boolean temp = true;
            for (Class c : classZz.getInterfaces()) {
                if (COLLECT_CLASS_TYPE.contains(c.getTypeName())) {
                    temp = false;
                    break;
                }
            }
            if(COLLECT_CLASS_TYPE.contains(typeReference.getTypeName())){
                temp = false;
            }
            if (temp) {
                //如果泛型的东西不是集合相关，那么就做普通对象处理
                return parse(scriptObjectMirror, typeReference);
            }
        }else{
            //给个空的，避免太深了遇到麻烦
            typeReference = new TypeReference(){};
        }
        if (scriptObjectMirror.isArray()) {
            List list = new ArrayList(scriptObjectMirror.size());
            for (int i = 0; i < scriptObjectMirror.size(); i++) {
                ScriptObjectMirror scriptObjectMirror1 = (ScriptObjectMirror)scriptObjectMirror.getSlot(i);
                //递归，集合里面的不知道是什么类型。如果泛型有给类型的话，用泛型的，没有的话就是null
                list.add(collectionParse(scriptObjectMirror1,typeReference.getSubTypeReference()));
            }
            return list;
        } else {
            //设置大小为 数据长度*1.25，避免因为扩容而浪费时间，map需要1.25，忘了源码里面写的啥了，他写的0.75
            Double size = scriptObjectMirror.size() * 1.25;
            int capacity = size.intValue() + 1;
            Map map = new HashMap(capacity);
            for (String key : scriptObjectMirror.keySet()) {
                Object object = scriptObjectMirror.get(key);
                if (BASE_CLASS_TYPE.contains(object.getClass().getSimpleName())) {
                    //如果是基础数据类型就直接塞给他
                    map.put(key, object);
                } else if ("ScriptObjectMirror".contains(object.getClass().getSimpleName())) {
                    //不是基础数据类型，递归处理
                    ScriptObjectMirror scriptObjectMirror1 = (ScriptObjectMirror)object;
                    //递归，集合里面的不知道是什么类型。如果泛型有给类型的话，用泛型的，没有的话就是null
                    map.put(key, collectionParse(scriptObjectMirror1,typeReference.getSubTypeReference()));
                }else{
                    //基础数据类型不知道有没有写全，反正除了ScriptObjectMirror对象，全部直接塞给他
                    map.put(key, object);
                }
            }
            return map;
        }
    }

//    public static String stringifyMap(Map<String, Object> hash){
//        StringBuilder buff = new StringBuilder();
//        buff.append("{");
//
//        int size = hash.size();
//        if(size > 0){
//            int i = 0;
//            for(String key : hash.keySet()){
//                buff.append("\"" + key + "\":" + "\"" + hash.get(key) + "\"");
//                if(++i != size)buff.append(",");
//            }
//        }
//        buff.append("}");
//        return buff.toString();
//    }

//    private static String navtiveStringify(Object jsonObj){
//        Object jsonStr = null;
//
//        try {
//            jsonStr = js.call("toSingleJSON", jsonObj);
//        } catch (ScriptException e) {
//            System.err.println("Can not make jsonObj as jsonStr in Rhino");
//            e.printStackTrace();
//        }
//
//        return jsonStr == null ? null : jsonStr.toString();
//    }


//    public static String stringifyObj(Object obj){
//        if (obj == null)return null;
//
//        // 检查是否可以交由 JS 转换的类型，否则使用该方法进行处理
////        if(obj instanceof NativeArray || obj instanceof NativeObject)return navtiveStringify(obj);
//
//        Class<?> clazz = obj.getClass();
//        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
//
//        StringBuilder buff = new StringBuilder();
//
//        buff.append("{");
//
//        for (java.lang.reflect.Field f : fields) {
//            String fieldName; Object value;
//
//            try{
//                fieldName = f.getName();
//                f.setAccessible(true);
//                if(fieldName.indexOf("this$") != -1)continue;
//                value = f.get(obj);
//
////	            System.out.println(fieldName + "--------" + value);
//                buff.append("\"");
//                buff.append(fieldName);
//                buff.append("\":");
//
//                if(value == null){
//                    buff.append("\"\",");
//                    continue;
//                }
//                if(value instanceof Boolean){
//                    buff.append((Boolean)value);
//                    buff.append(",");
//                }else if(value instanceof Number){
//                    buff.append((Number)value);
//                    buff.append(",");
//                }else if(value instanceof java.util.Date){
//                    buff.append("\"");
//                    buff.append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.util.Date)value));
//                    buff.append("\",");
//                }else if(value instanceof Object[]){
//                    Object[] arr = (Object[])value;
//                    String str = "";
//
//                    for(int i = 0; i < arr.length; i++){
//                        if(arr[i] instanceof String){
//                            str += ("\"" + arr[i] + "\"").replace("\\", "\\\\");
//                        }else{
//                            str += stringifyObj(arr[i]);
//                        }
//
//                        if(i != arr.length - 1)str += ",";
//                    }
//
//                    buff.append("[");
//                    buff.append(str);
//                    buff.append("]o");// ??????????????//
//                }else{
//                    buff.append("\"");
//                    buff.append(value.toString().replace("\\", "\\\\").replace("\"", "\\\""));
//                    buff.append("\",");
//                }
//
//            }catch(IllegalArgumentException e){
//                e.printStackTrace();
//            }catch (IllegalAccessException e) {
//                e.printStackTrace();
//                //}catch(java.lang.reflect.InvocationTargetException e) {
//                //e.printStackTrace();
//            }
//        }
//
//        if (buff.length() > 1)buff = buff.deleteCharAt(buff.length() - 1);
//
//        buff.append("}");
//
//        return buff.toString();
//    }

    /**
     * @param obj
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static String getJsonString(Object obj) throws InstantiationException, IllegalAccessException  {

        //如果传入的对象为集合
        if (obj instanceof Collection) {

            //执行collectionTojson方法将集合类型转为json格式
            return collectionTojson(obj);

        }

        //如果获取的对象类型为一个Map集合
        else if (obj instanceof Map) {

            //执行mapTojson方法将Map类型转为json格式
            return mapTojson(obj);

        }

        //如果获取的对象类型为普通对象
        else{

            //执行classTojson方法将普通类型转为json格式
            return classTojson(obj);

        }
    }

    /**
     * @描述 将集合转为json格式的字符串
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static String collectionTojson(Object obj) throws IllegalArgumentException, IllegalAccessException{

        //定义一个StringBuffer类型的字符串
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");

        Class<? extends Object> clazz = obj.getClass();

        //获取类中所有的字段
        Field[] declaredFields = clazz.getDeclaredFields();

        //设置可以获得私有字段的value
        Field.setAccessible(declaredFields, true);

        //定义全局变量
        boolean listf = false;
        boolean setf = false;
        Set<Object> set = null;
        List<Object> list = null;

        //遍历获取到的所有字段
        for (Field field : declaredFields) {

            //getDeclaringClass()同getClasses()，但不局限于public修饰，只要是目标类中声明的内部类和接口均可

            String simpleName = clazz.getSimpleName();

            //判断获取到的类型
            if(simpleName.equals("ArrayList")||simpleName.equals("LinkedList")){
                list = (List<Object>) obj;
                listf=true;
            }
            if(simpleName.equals("HashSet")||simpleName.equals("TreeSet")){
                set = (Set<Object>) obj;
                setf=true;
            }

        }


        //如果获取的对象类型为一个List集合
        if(listf == true){

            return listTojson(buffer, list).toString();

        }

        //如果获取的对象类型为一个Set集合
        if(setf == true){

            buffer = setTojson(set, buffer);

        }

        buffer.append("]");

        return buffer.toString();
    }


    /**
     * 描述 将基本类转为json格式
     * @param obj
     * @return
     */
    private static String classTojson(Object obj){

        //通过反射获取到类
        Class<? extends Object> clazz = obj.getClass();

        //获取类中所有的字段
        Field[] fields = clazz.getDeclaredFields();

        StringBuffer buffer = new StringBuffer();
        buffer.append("{");

        //设置setAccessible方法能获取到类中的私有属性和方法
        Field.setAccessible(fields, true);

        //遍历所有的方法和属性
        for (Field field : fields) {

            try {

                Object object = field.get(obj);

                //获取到该属性对应类型名称
                String fieldName = field.getType().getSimpleName();

                //如果该属性的值为空
                if(object == null){

                    //根据类型判断追加的值
                    if(fieldName.equals("String"))
                    {
                        buffer.append("\""+field.getName()+"\":\"\",");
                    }
                    else if(fieldName.equals("Boolean")||fieldName.equals("Integer")||fieldName.equals("Double")||fieldName.equals("Float")||fieldName.equals("Long")){

                        buffer.append("\""+field.getName()+"\":0,");
                    }

                    else{
                        buffer.append("\""+field.getName()+"\":null,");
                    }

                }
                else{

                    //获取到该属性的值对应的类
                    Class<? extends Object> fieldclass = object.getClass();
                    String simpleName = fieldclass.getSimpleName();

                    if(simpleName.equals("String")){

                        buffer.append("\""+field.getName()+"\":\""+field.get(obj)+"\",");

                    }
                    else if(simpleName.equals("Boolean")||simpleName.equals("Integer")||simpleName.equals("Double")||simpleName.equals("Float")||simpleName.equals("Long")){

                        buffer.append("\""+field.getName()+"\":"+field.get(obj)+",");

                    }
                    else if(simpleName.equals("Date")){

                        Date date = (Date) object;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String simdate = simpleDateFormat.format(date);
                        buffer.append("\""+field.getName()+"\":\""+simdate+"\",");

                    }
                    else if(simpleName.equals("ArrayList")||simpleName.equals("LinkedList"))
                    {
                        //将获取到的值强转为list集合
                        List<Object> list = (List<Object>) object;
                        buffer.append("\""+field.getName()+"\":[");

                        //执行listTojson方法将获取到的list转为json格式
                        buffer = listTojson(buffer, list).append("]");
                    }
                    else if(simpleName.equals("HashSet")||simpleName.equals("TreeSet"))
                    {
                        //将获取到的值强转为set集合
                        buffer.append("\""+field.getName()+"\":[");
                        Set<Object> set = (Set<Object>) object;

                        //执行setTojson方法将获取到的set转为json格式
                        buffer = setTojson(set, buffer).append("]");

                    }
                    else if(simpleName.equals("HashMap")||simpleName.equals("HashTable"))
                    {
                        buffer.append("\""+field.getName()+"\":");

                        //执行mapTojson方法将获取到的map对象转为json格式
                        StringBuffer mapbuffer = new StringBuffer(mapTojson(object));
                        mapbuffer.deleteCharAt(0);
                        buffer.append(mapbuffer);
                    }
                    else{

                        buffer = beanTojson(object,buffer).append(",");
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        buffer = new StringBuffer(buffer.substring(0,buffer.length()-1));
        buffer.append("}");

        return buffer.toString();
    }

    /**
     * 描述 将map集合转为json格式
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static String mapTojson(Object obj) throws IllegalArgumentException, IllegalAccessException{

        StringBuffer buffer = new StringBuffer();
        Class<? extends Object> clazz = obj.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        Field.setAccessible(declaredFields, true);
        buffer.append("[");
        Map<Object,Object> map = (Map<Object, Object>) obj;

        //通过Map.entrySet使用iterator(迭代器)遍历key和value
        Set<Entry<Object, Object>> set = map.entrySet();
        Iterator iterator = set.iterator();
        buffer.append("{");

        while (iterator.hasNext()){

            //使用Map.Entry接到通过迭代器循环出的set的值
            Map.Entry  mapentry = (Map.Entry) iterator.next();
            Object value = mapentry.getValue();

            //使用getKey()获取map的键，getValue()获取键对应的值
            String valuename ="";
            if (value!=null)
            valuename = value.getClass().getSimpleName();
            if("String".equals(valuename)){

                buffer.append("\""+mapentry.getKey()+"\":\""+mapentry.getValue()+"\",");
            }
            else if("Boolean".equals( valuename)||"Integer" .equals(valuename)||"Double".equals(valuename) ||"Float".equals(valuename) ||"Long".equals(valuename) ){

                buffer.append("\""+mapentry.getKey()+"\":"+mapentry.getValue()+",");
            }
            else if( "Date".equals(valuename)){
                Date date = (Date) value;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String simdate = simpleDateFormat.format(date);
                buffer.append("\""+mapentry.getKey()+"\":\""+simdate+"\",");
            }else if("ArrayList".equals(valuename) ||"LinkedList".equals(valuename) )
            {
                List<Object> list = (List<Object>) value;
                buffer.append("\""+mapentry.getKey()+"\":[");
                buffer = listTojson(buffer, list).append("]");
            }
            else if("HashSet".equals(valuename) ||"TreeSet".equals(valuename) )
            {
                buffer.append("\""+mapentry.getKey()+"\":[");
                Set<Object> sets = (Set<Object>) value;
                buffer = setTojson(sets, buffer).append("]");
            }
            else if("HashMap".equals(valuename) ||"HashTable".equals(valuename) )
            {
                buffer.append("\""+mapentry.getKey()+"\":");
                StringBuffer mapbuffer = new StringBuffer(mapTojson(value));
                mapbuffer.deleteCharAt(0);
                buffer.append(mapbuffer);
            }
            else{
                buffer.append("\""+mapentry.getKey()+"\":");
                buffer.append("{");

                Class<? extends Object> class1=Object.class;
                if (value!=null)
                class1 = value.getClass();
                Field[] fields = class1.getDeclaredFields();
                Field.setAccessible(fields, true);

                for (Field field : fields) {

                    Object object = field.get(value);
                    String fieldName = field.getType().getSimpleName();

                    if(object == null){
                        if(fieldName.equals("String"))
                        {
                            buffer.append("\""+field.getName()+"\":\"\",");
                        }

                        else{
                            buffer.append("\""+field.getName()+"\":null,");
                        }

                    }
                    else{

                        Class<? extends Object> fieldclass = field.get(value).getClass();
                        String simpleName = fieldclass.getSimpleName();
                        if(simpleName.equals("String")){

                            buffer.append("\""+field.getName()+"\":\""+field.get(value)+"\",");
                        }
                        else if(simpleName.equals("Boolean")||simpleName.equals("Integer")||simpleName.equals("Double")||simpleName.equals("Float")||simpleName.equals("Long")){

                            buffer.append("\""+field.getName()+"\":"+field.get(value)+",");
                        }
                        else if(simpleName.equals("Date")){
                            Date date = (Date) object;
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String simdate = simpleDateFormat.format(date);
                            buffer.append("\""+field.getName()+"\":\""+simdate+"\",");
                        }
                        else if(simpleName.equals("ArrayList")||simpleName.equals("LinkedList")){

                            List<Object> list = (List<Object>) object;
                            buffer.append("\""+field.getName()+"\":[");
                            StringBuffer append = listTojson(buffer, list).append("]");
                            buffer.append(append);
                        }
                        else if(simpleName.equals("HashSet")||simpleName.equals("TreeSet")){

                            buffer.append("\""+field.getName()+"\":[");
                            Set<Object> sets = (Set<Object>) object;
                            buffer = setTojson(sets, buffer).append("]");
                        }
                        else if(simpleName.equals("HashMap")||simpleName.equals("HashTable")){

                            buffer.append("\""+field.getName()+"\":");
                            StringBuffer mapbuffer = new StringBuffer(mapTojson(object));
                            mapbuffer.deleteCharAt(0);
                            buffer.append(mapbuffer);
                        }
                        else{
                            buffer = beanTojson(object,buffer).append(",");

                        }
                    }

                }

                buffer =  new StringBuffer(""+buffer.substring(0,buffer.length()-1)+"");
                buffer.append("},");
            }

        }

        buffer =  new StringBuffer(""+buffer.substring(0,buffer.length()-1)+"");
        return buffer.toString()+"}]";
    }

    /**
     * @描述 将不是基本类型的字段转为json格式
     * @param obj
     * @param buffer
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static StringBuffer beanTojson(Object obj,StringBuffer buffer) throws IllegalArgumentException, IllegalAccessException{

        Class<? extends Object> clazz = obj.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        Field.setAccessible(declaredFields, true);

        buffer.append("\""+clazz.getSimpleName()+"\":{");

        for (Field field : declaredFields) {

            Object object = field.get(obj);
            String fieldName = field.getType().getSimpleName();

            if(object == null){
                if(fieldName.equals("String"))
                {
                    buffer.append("\""+field.getName()+"\":\"\",");
                }

                else{
                    buffer.append("\""+field.getName()+"\":null,");
                }

            }
            else{

                Class<? extends Object> fieldclass = object.getClass();
                String simpleName = fieldclass.getSimpleName();

                if(simpleName.equals("String")){

                    buffer.append("\""+field.getName()+"\":\""+field.get(obj)+"\",");
                }
                else if(simpleName.equals("Boolean")||simpleName.equals("Integer")||simpleName.equals("Double")||simpleName.equals("Float")||simpleName.equals("Long")){

                    buffer.append("\""+field.getName()+"\":"+field.get(obj)+",");
                }
                else if(simpleName.equals("Date")){

                    Date date = (Date) object;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String simdate = simpleDateFormat.format(date);
                    buffer.append("\""+field.getName()+"\":\""+simdate+"\",");
                }
                else if(simpleName.equals("ArrayList")||simpleName.equals("LinkedList")){

                    List<Object> list = (List<Object>) object;
                    buffer = listTojson(buffer, list);
                }
                else if(simpleName.equals("HashSet")||simpleName.equals("TreeSet")){

                    Set<Object> set = (Set<Object>) object;
                    buffer = setTojson(set, buffer);
                }
                else if(simpleName.equals("HashMap")||simpleName.equals("HashTable")){

                    buffer.append("\""+field.getName()+"\":");
                    StringBuffer mapbuffer = new StringBuffer(mapTojson(object));
                    mapbuffer.deleteCharAt(0);
                    buffer.append(mapbuffer);
                }
                else{
                    buffer = beanTojson(object,buffer).append("}");
                }
            }

        }

        buffer =  new StringBuffer(""+buffer.substring(0,buffer.length()-1)+"");
        buffer.append("}");

        return buffer;
    }

    /**
     * @描述 将list数组转为json格式
     * @param buffer
     * @param list
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static StringBuffer listTojson(StringBuffer buffer,List list) throws IllegalArgumentException, IllegalAccessException{

        //遍历传过来的list数组
        for (Object object : list) {

            //判断遍历出的值是否为空
            if (object == null) {
                buffer.append(",");
            }
            else{

                Class<? extends Object> class1 = object.getClass();
                String simpleName = class1.getSimpleName();

                if(simpleName.equals("String")){

                    buffer.append("\""+object.toString()+"\",");
                }
                else if(simpleName.equals("Boolean")||simpleName.equals("Integer")||simpleName.equals("Double")||simpleName.equals("Float")||simpleName.equals("Long")){

                    buffer.append(""+object.toString()+",");
                }
                else if(simpleName.equals("Date")){
                    Date date = (Date) object;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String simdate = simpleDateFormat.format(date);
                    buffer.append(""+simdate+",");
                }
                else{

                    Class<? extends Object> class2 = object.getClass();
                    Field[] fields = class2.getDeclaredFields();
                    Field.setAccessible(fields, true);
                    buffer.append("{");
                    //遍历对象中的所有字段获取字段值和字段名称拼成json字符串
                    for (Field field : fields) {

                        Object fieldobj = field.get(object);
                        String fieldName = field.getType().getSimpleName();

                        if(fieldobj == null){

                            if(fieldName.equals("String"))
                            {
                                buffer.append("\""+field.getName()+"\":\"\",");
                            }

                            else{
                                buffer.append("\""+field.getName()+"\":null,");
                            }

                        }

                        else{

                            String fsimpleName = fieldobj.getClass().getSimpleName();

                            if(fsimpleName.equals("String")){

                                buffer.append("\""+field.getName()+"\":\""+field.get(object)+"\",");
                            }
                            else if(fsimpleName.equals("Boolean")||fsimpleName.equals("Integer")||fsimpleName.equals("Double")||fsimpleName.equals("Float")||fsimpleName.equals("Long")){

                                buffer.append("\""+field.getName()+"\":"+field.get(object)+",");
                            }
                            else if(fsimpleName.equals("Date")){

                                Date date = (Date) fieldobj;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                String simdate = simpleDateFormat.format(date);
                                buffer.append("\""+field.getName()+"\":"+simdate+",");
                            }
                            else if (fsimpleName.equals("ArrayList")) {
//                                System.out.println(fieldobj.toString().substring(1,fieldobj.toString().length()-1));
                                //执行collectionTojson方法将集合类型转为json格式
                                buffer =buffer.append("\"").append(field.getName()).append("\"").append(":").append(collectionTojson(Arrays.asList(fieldobj.toString().substring(1,fieldobj.toString().length()-1).split(",")))) .append(",");
                            }

                            else{

                                buffer = beanTojson(fieldobj, buffer).append(",");
                            }
                        }

                    }

                    buffer =  new StringBuffer(""+buffer.substring(0,buffer.length()-1)+"");
                    buffer.append("},");
                }
            }

        }

        buffer =  new StringBuffer(""+buffer.substring(0,buffer.length()-1)+"");
        buffer.append("]");

        return buffer;
    }



    /**
     * @描述 将set数组转为json格式
     * @param set
     * @param buffer
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static StringBuffer setTojson(Set set,StringBuffer buffer) throws IllegalArgumentException, IllegalAccessException{

        for (Object object : set) {
            if (object == null) {
                buffer.append(""+"null"+",");
            }
            else{

                Class<? extends Object> class1 = object.getClass();

                //判断集合中的值是否为java基本类型
                String simpleName = class1.getSimpleName();
                if(simpleName.equals("String")){

                    buffer.append("\""+object.toString()+"\",");
                }
                else if(simpleName.equals("Boolean")||simpleName.equals("Integer")||simpleName.equals("Double")||simpleName.equals("Float")||simpleName.equals("Long")){

                    buffer.append(""+object.toString()+",");
                }
                else if(simpleName.equals("Date")){

                    Date date = (Date) object;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String simdate = simpleDateFormat.format(date);
                    buffer.append(""+simdate+",");
                }
                else{

                    Class<? extends Object> class2 = object.getClass();
                    Field[] fields = class2.getDeclaredFields();
                    Field.setAccessible(fields, true);
                    buffer.append("{");

                    //遍历对象中的所有字段获取字段值和字段名称拼成json字符串
                    for (Field field : fields) {

                        Object fieldobj = field.get(object);
                        String fieldName = field.getType().getSimpleName();

                        if(object == null){
                            if(fieldName.equals("String"))
                            {
                                buffer.append("\""+field.getName()+"\":\"\",");
                            }

                            else{
                                buffer.append("\""+field.getName()+"\":null,");
                            }

                        }
                        else{

                            String fsimpleName = fieldobj.getClass().getSimpleName();
                            if(fsimpleName.equals("String")){

                                buffer.append("\""+field.getName()+"\":\""+field.get(object)+"\",");
                            }
                            else if(fsimpleName.equals("Boolean")||fsimpleName.equals("Integer")||fsimpleName.equals("Double")||fsimpleName.equals("Float")||fsimpleName.equals("Long")){

                                buffer.append("\""+field.getName()+"\":"+field.get(object)+",");
                            }
                            else if(fsimpleName.equals("Date")){

                                Date date = (Date) object;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                String simdate = simpleDateFormat.format(date);
                                buffer.append("\""+field.getName()+"\":"+simdate+",");
                            }
                            else{

                                buffer = beanTojson(fieldobj, buffer).append(",");
                            }
                        }
                    }

                    buffer =  new StringBuffer(""+buffer.substring(0,buffer.length()-1)+"");
                    buffer.append("},");
                }
            }
        }

        buffer =  new StringBuffer(""+buffer.substring(0,buffer.length()-1)+"");
        return buffer;
    }


}
