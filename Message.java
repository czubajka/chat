//Message.java
import java.io.*;

/** Klasa definiująca rodzaje wiadomości wymienianych pomiędzy klientem a serwerem.
 * Implementuje serializację w celu łatwej wysyłki obiektu wiadomości na strumień
 *@author Karolina Nędza-Sikoniowska
 *@version 1.0
 */
public class Message implements Serializable 
{

	/** Stała definiujące typ wiadomości wysłanej przez klienta:
	 * MESSAGE - wiadomość tekstowa */
	static final int MESSAGE = 0; 
	/** Stała definiujące typ wiadomości wysłanej przez klienta:
	 * CLIENTS_ONLINE - prośba wyświetlenia informacji o klientach będących online */
	static final int CLIENTS_ONLINE = 1;
	/** Stała definiujące typ wiadomości wysłanej przez klienta:
	 * LOGOUT - prośba wylogowania */
	static final int LOGOUT = 2;
	/** typ instancji (MESSAGE, CLIENTS_ONLINE lub LOGOUT) */
	private int type;
	/** treść wiadomości */
	private String message;
	
	/**
	 * Konstruktor.
	 * @param type typ wiadomości (MESSAGE, CLIENTS_ONLINE lub LOGOUT)
	 * @param message treść wiadomości
	 */
	Message(int type, String message) 
	{
		this.type = type;
		this.message = message;
	}
	
	/**
	 * Metoda zwracająca typ wiadomości
	 * @return typ wiadomości, na rzecz której wywołano metodę
	 */
	public int getType() 
	{
		return type;
	}
	
	/**
	 * Metoda zwracająca treść wiadomości
	 * @return treść wiadomości, na rzecz której wywołano metodę
	 */
	public String getMessage() 
	{
		return message;
	}
}
