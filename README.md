# Multi-Threaded-Star-Wars-Attack-Simulation
Project 2/3 in course Systems Programming. Main purpose is to create a system of simulating multi threaded Star Wars attack.
The project works in a few different stages of attacks,where an attack relies on past stages using callbacks.
Attacking in the project was implemented by resources allocation and handle the synchronization of the resources allocation.

Input: Json file which contains: 
-Attacks array description: which resources (serial number) we need for the specific attack (resources- Ewoks), duration of the attack.

-Duration of the next stages after finishing the main attack (as described above). To be more specific, "R2D2":(duration) "Lando":(duration).
  
-Number of resources (Ewoks- the number also points at the serial numbers).
  
Output: Json file which contains:
  
-Total attacks quantity
  
-Time stamp  which each thread terminated.
  

How to Operate:
1)run: mvn compile
  
2)(Optional) mvn test (Unit tests implemented to check the correctness of the code)
  
3)mvn exec:java -Dexec.mainClass="bgu.spl.mics.application.Main" -Dexec.args="./input1.json(or 2/3/4/5) ./output.json "
