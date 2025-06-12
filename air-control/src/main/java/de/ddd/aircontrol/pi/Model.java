package de.ddd.aircontrol.pi;

public enum Model
{
	// https://www.pi4j.com/getting-started/understanding-the-pins/
	PI_3_B;
	
	public int getNumPins()
	{
		return 40;
	}
	
	public PinType getType(int pin)
	{
		return switch(pin)
			{
				case 1, 17
					-> PinType.POWER_3_3V;
					
				case 2, 4
					-> PinType.POWER_5V;
					
				case 6, 9, 14, 20, 25, 30, 34, 39
					-> PinType.GROUND;
					
				default -> PinType.GPIO;
			};
	}
	
	public int toGpioPin(int pin)
	{
		return switch(pin)
			{
				case 3 -> 2;
				case 5 -> 3;
				case 7 -> 4;
				case 8 -> 14;
				case 10 -> 15;
				case 11 -> 17;
				case 12 -> 18;
				case 13 -> 27;
				case 15 -> 22;
				case 16 -> 23;
				case 18 -> 28;
				case 19 -> 10;
				case 21 -> 9;
				case 22 -> 25;
				case 23 -> 11;
				case 24 -> 8;
				case 26 -> 7;
				case 27 -> 0;
				case 28 -> 1;
				case 29 -> 5;
				case 31 -> 6;
				case 32 -> 12;
				case 33 -> 13;
				case 35 -> 19;
				case 36 -> 16;
				case 37 -> 26;
				case 38 -> 20;
				case 40 -> 21;
				default -> -1;
			};
	}
	
	public int toPin(int gpiopin)
	{
		return switch(gpiopin)
			{
				case 0 -> 27;
				case 1 -> 28;
				case 2 -> 3;
				case 3 -> 5;
				case 4 -> 7;
				case 5 -> 29;
				case 6 -> 31;
				case 7 -> 26;
				case 8 -> 24;
				case 9 -> 21;
				case 10 -> 19;
				case 11 -> 23;
				case 12 -> 32;
				case 13 -> 33;
				case 14 -> 8;
				case 15 -> 10;
				case 16 -> 36;
				case 17 -> 11;
				case 18 -> 12;
				case 19 -> 35;
				case 20 -> 38;
				case 21 -> 40;
				case 22 -> 15;
				case 23 -> 16;
				case 24 -> 18;
				case 25 -> 22;
				case 26 -> 37;
				case 27 -> 13;
				default -> -1;
			};
	}
}