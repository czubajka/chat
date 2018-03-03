//StartClient.java
import javax.swing.JFrame;

/** Klasa uruchamiająca  klasę 
 * @see Client i 
 * @see ServerGUI
*@author Karolina Nędza-Sikoniowska
*@version 1.0
*/
public class StartClient
{
	/** domyślny numer portu */
	private final static int PORT_NUMBER = 2222;
	/** domyślny adres serwera */
	private final static String SERVER_ADDRESS = "localhost";
	
	/** Metoda główna klasy @see StartClient
	 * uruchamiająca program kliencki i ustawiająca domyślny wygląd okienek. 
	 *@param args tablica argumentów wejściowych
	 */
	public static void main(String[] args) 
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		new ClientGUI(SERVER_ADDRESS, PORT_NUMBER);
	}
}