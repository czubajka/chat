//Client.java
import java.net.*;
import java.io.*;
import java.util.*;

/** Klasa definiująca działanie programu klienckiego komunikatora sieciowego.
 * Uruchamiana z klasy ClientGUI
 *@author Karolina Nędza-Sikoniowska
 *@version 1.0
 */
public class Client  
{
	/** gniazdko klienta */
	private Socket socket;
	/** obiekt obsługujący strumień wejściowy */
	private ObjectInputStream inputStream;	
	/** obiekt obsługujący strumień wyjściowy */
	private ObjectOutputStream outputStream;		
	/** referencja do GUI */
	private ClientGUI gui;
	/** adres serwera */
	private String server;
	/** numer portu */
	private int port;
	/** nazwa użytkownika */
	private String username;

	/**
	 *  Konstruktor instancji klasy Client
	 *  @param server nazwa servera
	 *  @param port numer portu
	 *  @param username nazwa użytkownika
	 *  @param gui referencja do GUI
	 */
	Client(String server, int port, String username, ClientGUI gui) 
	{
		this.server = server;
		this.port = port;
		this.username = username;
		this.gui = gui;
	}
	
	/**
	 * Metoda łącząca klienta z serwerem
	 * @return zwraca true jeśli udało się połączyć, w przeciwnym wypadku zwraca false
	 */
	public boolean start() 
	{
		try 
		{
			socket = new Socket(server, port);
		} 
		catch(Exception e) 
		{
			show("Błąd, nie udało się połączyć :  " + e);
			return false;
		}
		show("Połączono!");
		try
		{
			inputStream  = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException ioe) 
		{
			show("Wyjątek przy tworzeniu strumieni IO : " + ioe);
			return false;
		}
		new ListenFromServer().start();			//tworzy wątek do nasłuchiwania z serwera
		try
		{
			outputStream.writeObject(username);			//wysyła nazwę użytkownika
		}
		catch (IOException ioe) 
		{
			show("Wyjątek przy wysyłaniu nazwy użytkownika : " + ioe);
			disconnect();
			return false;
		}
		return true;
	}

	/**
	 * Wysyła wiadomość na serwer
	 * @param message wiadomość do wysłania
	 */
	void sendMessage(Message message) 
	{
		try {
			outputStream.writeObject(message);
		}
		catch(IOException e) 
		{
			show("Błąd podczas wysyłania wiadomości na serwer : " + e);
		}
	}
	
	/**
	 * Wysyła tekst do okna rozmowy gui 
	 * @param s tekst do wysłania
	 */
	void show(String s) 
	{
			gui.showInTalkArea(s);
	}
	
	/**
	 * Metoda zamykająca strumienie i wtyczkę, resetująca wszystkie ustawienia,
	 * wywoływana w przypadku błędu
	 */
	void disconnect() 
	{
		try 
		{ 
			if(inputStream != null) 
				{
					inputStream.close();
				}
		}
		catch(Exception e) 
			{
				show(" " + e);
			}
		try 
		{
			if(outputStream != null) 
				{
					outputStream.close();
				}
		}
		catch(Exception e) 
		{
			show(" " + e);
		}
        try
        {
			if(socket != null) 
				{
					socket.close();
				}
		}
		catch(Exception e) 
        {
			show(" " + e);
        }
        gui.reset();	
	}

	/** Klasa obsługująca wątek klient-serwer, oczekuje na informacje z serwera (wiadomości),
	 * po odebraniu których, umieszcza je w oknie rozmowy talkArea
	 */
	class ListenFromServer extends Thread 
	{
		public void run() 
		{
			while(true) 
			{
				try 
				{
					String m = (String) inputStream.readObject();
					gui.showInTalkArea(m);
				}
				catch(IOException e) 
				{
					show("Połączenie zakończone.");
					gui.reset();
					break;
				}
				catch(ClassNotFoundException cnfe) {}
			}
		}
	}
}
