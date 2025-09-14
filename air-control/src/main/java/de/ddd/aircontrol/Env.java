package de.ddd.aircontrol;

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
	private final Settings settings;
	private final Ventilation ventilation;
	
	private final ControllerManual controllerManual;
	private final Controller controller;
	
	public Env(Sensors sensors, Pi pi, Settings settings, Ventilation ventilation, Controller controller)
	{
		super();
		this.sensors = sensors;
		this.pi = pi;
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
