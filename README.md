# hotel_booking_manager
I implement a simple hotel booking manager in Java that can meet the following functions:

1. A method to store a booking. A booking consists of a guest name, a room number,
  and a date.
  
2. A method to find the available rooms on a given date.
  
3. A method to find all the bookings for a given guest.
  

I will demonstrate these functions below:

1. To make the number of rooms configurable, I use a request like " http://localhost:8080/initiateRoom?number=20" to initiate rooms. The parameter number is the number of rooms. A JSON string such as"{
  
  "res": "Initiate success!",
  
  "data": "[{"romeId":"1","status":"0"}]"
  
  }" is returned to indicate whether the operation is successful.
  
2. To store a booking, I use a request like "http://localhost:8080/add?guestName=xiaopingguo&roomNumber=2&bookingDate=2022-03-22" to save the booking.
  
3. To find the available rooms on a given date, I use a request like "http://localhost:8080/findRoom?date=2022-03-22" to find the available rooms.
  
4. To find all the bookings for a given guest, I use a request like "http://localhost:8080/findBooking?guestId=1&guestName=xiaopingguo" to find all the bookings for the given guest.
  

Due to lack of experience and time, this program still has some defects. The first disadvantage is that I didn't split the services in a way of micro services, but combined these services together, and started the program by configuring Tomcat. Second, for thread safety, jedis was originally intended to be used to implement write lock, but because the data structure of hset is used, the operation lock is more complex, due to lack of time and ability, it has not been investigated clearly.
