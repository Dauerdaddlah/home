package de.ddd.aircontrol.pi;

public interface PiPin
{
	/**
	 * @return the pinmode for this pin, {@link PinMode#UNKNOWN} knows that it is not used currently
	 */
	public PinMode getPinMode();
	/**
	 * set the pin-mode for this pin.<br>
	 * It may not be possible to change the mode once it is already set
	 * 
	 * @param mode the mode for this pin
	 * @throws IllegalStateException if this pin already has a pinmode
	 */
	public void setPinMode(PinMode mode) throws IllegalStateException;
	
	/**
	 * Get the digital value of this pin. Only valid in modes {@link PinMode#DIGITAL_IN} and {@link PinMode#DIGITAL_OUT}
	 * 
	 * @return the digital value of this pin
	 * @throws IllegalStateException if this pin is in no digital mode currently
	 */
	public boolean getDigitalValue() throws IllegalStateException;
	/**
	 * Set the digital value for this pin. This is only valid in mode {@link PinMode#DIGITAL_OUT}
	 * 
	 * @param value the new value for this pin. True means 3.3v, false means 0v
	 * @throws IllegalStateException if this pin is not in correct mode
	 */
	public void setDigitalValue(boolean value) throws IllegalStateException;
	/**
	 * Get the analog value of this pin in range ???. This is only valid in mode {@link PinMode#ANALOG_IN} and {@link PinMode#ANALOG_OUT}
	 * 
	 * @return the analog value of this pin in range ???
	 * @throws IllegalStateException if this pin is in no analog mode
	 */
	public int getAnalogValue() throws IllegalStateException;
	/**
	 * Set the analog value of this pi. Only valid in mode {@link PinMode#ANALOG_OUT}
	 * 
	 * @param value the new analog value for this pin
	 * @throws IllegalStateException if this pin is in wrong mode
	 */
	public void setAnalogValue(int value) throws IllegalStateException;
}
