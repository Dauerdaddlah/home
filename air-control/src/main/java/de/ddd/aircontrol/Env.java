package de.ddd.aircontrol;

import de.ddd.aircontrol.cleaning.Cleanings;
import de.ddd.aircontrol.cleaning.VentilatorCleaning;
import de.ddd.aircontrol.control.Controller;
import de.ddd.aircontrol.control.ControllerManual;
import de.ddd.aircontrol.pi.Pi;
import de.ddd.aircontrol.sensor.SensorData;
import de.ddd.aircontrol.sensor.Sensors;
import de.ddd.aircontrol.settings.Settings;
import de.ddd.aircontrol.ventilation.Ventilation;

public class Env
{
	private final Sensors sensors;
	private final Pi pi;
	private final Cleanings cleanings;
	private final Settings settings;
	private final Ventilation ventilation;
	
	private final ControllerManual controllerManual;
	private final Controller controller;
	
	public Env(Sensors sensors, Pi pi, Cleanings cleanings, Settings settings, Ventilation ventilation, Controller controller)
	{
		super();
		this.sensors = sensors;
		this.pi = pi;
		this.cleanings = cleanings;
		this.settings = settings;
		this.ventilation = ventilation;
		
		this.controller = controller;
		this.controllerManual = new ControllerManual(this.controller);
	}
	
	public Sensors sensors()
	{
		return sensors;
	}
	
	public SensorData sensor(String sensorName)
	{
		return sensors.getData(sensorName);
	}
	
	public Cleanings cleanings()
	{
		return cleanings;
	}
	
	public VentilatorCleaning cleaning(int number)
	{
		return cleanings.getCleaning(number);
	}
	
	public Pi pi()
	{
		return pi;
	}
	
	public Ventilation ventilation()
	{
		return ventilation;
	}
	
	public Settings settings()
	{
		return settings;
	}
	
	public Controller controller()
	{
		return controller;
	}
	
	public ControllerManual controllerManual()
	{
		return controllerManual;
	}
}
