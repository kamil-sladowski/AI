
public interface AIInterface {
	/**
	 * Metoda ustawia dostep do interfejsu srodowiskowego gry.
	 * @param ei obiekt umozliwiajacy dostep do srodowiska gry
	 */
	public void setInterfaceToEnvironment( EnvironmentalInterface ei );
	
	/**
	 * Metoda ustawia dostep do interfejsu statku kosmicznego.
	 * @param sh obiekt umozliwiajacy dostep do interfejsu statku
	 */
	public void setInterfaceToShip( ShipInterface sh );
	
	/**
	 * Metoda przekazuje sterowanie statkiem do Sztucznej Inteligencji.
	 */
	public void start();
}
