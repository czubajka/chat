//StartServer.java
import javax.swing.JFrame;

/** Klasa uruchamiająca  klasę 
 * @see Server i 
 * @see ServerGUI
*@author Karolina Nędza-Sikoniowska
*@version 1.0
*/
public class StartServer
{
	/**port domyślny nasłuchiwania serwera */
	private final static int PORT_NUMBER = 2222;
	
	/** Metoda główna klasy @see StartServer
	 * uruchamiająca serwer z portem domyślnym StartServer#PORT_NUMBER 
	 * i ustawiająca domyślny wygląd okienek. 
	 *@param args tablica argumentów wejściowych
	 */
	public static void main(String[] args) 
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		new ServerGUI(PORT_NUMBER);
	}
}