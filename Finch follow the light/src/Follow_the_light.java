import edu.cmu.ri.createlab.terk.robot.finch.Finch;
public class Follow_the_light {
	static Finch myf = new Finch();
	static public int ambient_lighting_left = myf.getLeftLightSensor();
	static public int ambient_lighting_right = myf.getRightLightSensor();
	static public boolean switch_1;
	
	static public void main(String args[]) throws Exception{
		ambient();
		movement();
	}
	static public void ambient() throws Exception{ 
	System.out.println("recording ambient lighting");
	System.out.println("left sensor ambient light = " + ambient_lighting_left);			//maybe put outside methods as static variables
	System.out.println("right sensor ambient light = " + ambient_lighting_right);
	
	System.out.println("will not begin until a light brighter than ambient is shined on it");
	}
	static public void movement() throws Exception{
		switch_1 = true; 			//makes switch_1 true - causing while loop to be true - hence infinitely loop
		if(myf.isObstacle() == true)	{			//if there is an obstacle, the finch will stop - EVEN IF LIGHT SOURCE 
			switch_1=true;
			myf.setLED(255,0,0,10);			//red LED for stop
		}
		else{
			long start_delay = System.currentTimeMillis(); //allows 2 seconds for ambient light to be recorded, then light to be followed - BEFORE 5 second count down starts
			System.out.println("2 seconds to shine a brighter light source on the finch");
			while(switch_1){				//loop to contain functionality - goes on forever
				while (System.currentTimeMillis()  > (start_delay + 2000)){
					int difference;			//difference in light sensors
					int right_light = myf.getRightLightSensor(); 
					int left_light = myf.getLeftLightSensor();
					long current_time = System.currentTimeMillis();			//for recording time between cycles of following the light
					int left_wheel_vel;
					double left_wheel_vel_ms;
					int right_wheel_vel;
					double right_wheel_vel_ms;
					double outer_vel;
					double turning_angle = 0;
						myf.setLED(0,255,0,10);	
						myf.buzz (900,100);							
						difference = ((right_light - left_light) * 2);	//*2 to speed up process
						left_wheel_vel = (difference + 50);		//left wheel will turn forwards to angle itself towards the light source
						right_wheel_vel = (50 - difference);	//right wheel will turn backwards to angle towards the light source
						myf.setWheelVelocities((difference + 50), (50 - difference));
					long duration = (System.currentTimeMillis() - current_time);
					System.out.println("+++++++++++++");
					System.out.println("+++++++++++++"); 		//breaks apart logging 
					System.out.println("duration of movement = " + duration);
					System.out.println("left wheel speed = " + left_wheel_vel);
					System.out.println("right wheel speed = " + right_wheel_vel);
					if(left_wheel_vel > right_wheel_vel){
						System.out.println("turning right");
					}else if(left_wheel_vel < right_wheel_vel){
						System.out.println("turning left");
					}else{
						System.out.println("going straight"); 		//only hits else if left vel == right vel
					}
							myf.setWheelVelocities(left_wheel_vel,right_wheel_vel);
					
							left_wheel_vel_ms = (left_wheel_vel * (0.381/255));	//finch travels at 15inches per second at speed = 255 = 0.381 m/s 
							right_wheel_vel_ms = (right_wheel_vel * (0.381/255));
							
							if (left_wheel_vel_ms > right_wheel_vel_ms){		//as both wheels still turn, angle is calculated with the slower wheel being seen as a pivot point
								outer_vel = (left_wheel_vel_ms - right_wheel_vel_ms);
								turning_angle = (((outer_vel * duration)/0.09) * (180/Math.PI)); //changing from radians to degrees
							}
							if (right_wheel_vel_ms > left_wheel_vel_ms){
								outer_vel = (right_wheel_vel_ms - left_wheel_vel_ms);
								turning_angle = (((outer_vel * duration)/0.09) * (180/Math.PI));
							}
							turning_angle = turning_angle % 365; //taking modulo of the angle recorded due to very small time frames, errors occur
					System.out.println("turning angle = " + turning_angle);
			
					
					if (( (left_light < (ambient_lighting_left + 5)) & (right_light < (ambient_lighting_right +5)) )){
						System.out.println("no light detected - or lower than ambient lighting");
						myf.setLED(255,0,0,10);	
						myf.stopWheels();
						
						long before = System.currentTimeMillis(); 				//begins start point for 5 second timer
						while(System.currentTimeMillis() - before < 5000){		//at 5001 miliseconds (past 5 seconds) the loop will finish, and then proceed to close the application
							right_light = myf.getRightLightSensor(); //allows measurement of sensors inside the loop
							left_light = myf.getLeftLightSensor();
							if (( (left_light > (ambient_lighting_left + 5)) | (right_light > (ambient_lighting_right + 5 )) )){		//added 5 to ambient as light must be a true source, not fluctuations in ambient
								movement();
							}
						}
						System.out.println("no light for 5 seconds, exiting");
						System.exit(0);
					}
				}
			}
		}	
	}//movement method close
}