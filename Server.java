//Server.java
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Główna klasa obsuugująca pracę serwera.
 */
public class Server 
{
	
	/** referencja do GUI */
	private ServerGUI serverGUI;
	/** identyfikator połączenia*/
	private static int connectionId;
	/** lista przechowująca bieżące wątki klientów*/
	private ArrayList<ClientThread> list;
	/** zmienna numeru portu */
	private int port;
	/** zmienna typu boolean: "true" oznacza, �e serwer dziala*/
	private boolean working;
	/** zmienna do wy�wietlania czasu*/
	private SimpleDateFormat time;	
	/** socket */
	private Socket socket;
	
	/**
	 * Konstruktor tworzący instancję serwera
	 * @param port numer portu, 
	 * @param serverGUI referencja do utworzonego GUI
	 */
	public Server(int port, ServerGUI serverGUI) 
	{
		time = new SimpleDateFormat("HH:mm:ss");
		list = new ArrayList<ClientThread>();
		this.serverGUI = serverGUI;
		this.port = port;
	}
	
	/**
	 * Wyświetla w oknie zdarzeń GUI informację argumentu.
	 * @param info informacja do wyświetlenia
	 */
	private void showInfo(String info) 
	{
		String t = time.format(new Date()) + " " + info;
		serverGUI.showInEventsArea(t + "\n");
	}
	
	/**
	 * Metoda uruchamiająca serwer, tworząca ServerSocket
	 */
	public void start() 
	{
		working = true;
		try 
		{
			ServerSocket serverSocket = new ServerSocket(port);
			// pętla oczekiwania na klientów
			while(working) 
			{
				showInfo("Oczekiwanie na klientów na porcie nr " + port + ".");
				Socket socket = serverSocket.accept();		//zaakceptowanie klienta
				if(!working)
				{			//sprawdzenie, czy serwer nadal ma pracować
					break;
				}
				ClientThread client = new ClientThread(socket);  //tworzenie wątku klienta
				list.add(client);
				client.start();
			}
			//jeżli serwer ma zakończyć pracę
			try 
			{
				serverSocket.close();					//zamknięcie nasłuchiwania
				socket.close();							//zamknięcie socketa serwera
				for(int i = 0; i < list.size(); ++i) 	//zamykanie połączeń z poszczególnymi klientami
				{
					ClientThread client = list.get(i);
					try 
					{
						client.inputStream.close();
						client.outputStream.close();
						client.socket.close();
					}
					catch(IOException ioe) 
					{
			            showInfo(" Wyjątek: " + ioe + "\n");
					}
				}
			}
			catch(Exception e) 
			{
	            showInfo(" Wyjątek podczas tworzenia ServerSocket: " + e + "\n");
			}
		}
		catch (IOException ioe) 
		{
            showInfo(" Wyjątek podczas tworzenia ServerSocket: " + ioe + "\n");
		}
	}
	
    /**
     * Metoda zatrzymująca pracę serwera. W celu rozłączenia, serwer łączy się sam z sobą,
     * przy czym zmienna working ustawiana jest na false,
     * co skutkuje przejściem serwera do zamykania połączeń.
     */
	void stop() 
	{
		working = false;
		try 
		{
			socket = new Socket("localhost", port);
		}
		catch(Exception e) 
		{
            showInfo(" Wyjątek podczas próby połączenia się serwera z serwerem: " + e);
		}
	}
	
	/**
	 *  Wysyła wiadomości do wszystkich klientów, 
	 *  dodaje do wiadomości aktualny czas.
	 *  @param message wiadomość, która ma być wysłana
	 */
	private synchronized void sendMessage(String message) 
	{
		String t = time.format(new Date());
		String fullMessage = t + " " + message + "\n";
		serverGUI.showInTalkArea(fullMessage);		//dodanie do okna wiadomości
		
		// pętla w odwrotnej kolejności w celu łatwiejszego usunięcia niedostępnych klientów
		for(int i = (list.size()-1); i >= 0; i--) 		//wysyłanie wiadomości do wszystkich klientów
		{											
			ClientThread client = list.get(i);			//pobieranie klienta z listy
			if(!client.sendToClient(fullMessage)) 			//jeśli niepowodzenie, usuń klienta z listy
			{
				list.remove(i);
				showInfo("Klient " + client.username + " nieosiagalny - zostal usuniety z serwera.");
			}
		}
	}

	/**
	 * Dla klienta wylogowującego się wiadomością typu LOGOUT
	 * @param id id klienta, który ma być usunięty
	 */
	private synchronized void remove(int id) 
	{
		for(int i = 0; i < list.size(); i++) 
		{
			ClientThread client = list.get(i);
			if(client.id == id) {
				list.remove(i);
				return;
			}
		}
	}
	
	/** 
	 * Wątek serwera przydzielany osobno dla każdego połączonego klienta 
	 */
	class ClientThread extends Thread 
	{
		/** Gniazdko do połączenia klienta */
		Socket socket;		
		/** ID wątku/klienta*/
		int id;
		/** Strumień wejściowy obiektu */
		ObjectInputStream inputStream;
		/** Strumień wyjściowy obiektu */
		ObjectOutputStream outputStream;
		/**nazwa użytkownika/klienta */
		String username;
		/** Zmienna do operacji na wiadomościach wątku */
		Message message;

		/**
		 * Konstruktor wątku
		 * @param socket gniazdko połączenia klienta
		 */
		ClientThread(Socket socket) 
		{
			id = ++connectionId;			// tworzenie unikatowego ID
			this.socket = socket;
			try
			{
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				inputStream  = new ObjectInputStream(socket.getInputStream());
				username = (String) inputStream.readObject();
				showInfo(username + " polaczony.");
			}
			catch (IOException e) 
			{
				showInfo("Wyjatek przy tworzeniu strumieni wejscia/wyjscia: " + e);
				return;
			}
			catch (ClassNotFoundException e) 
			{
				showInfo("Wyjatek pobierania informacji o kliencie: " + e);
			}
		}

		/**
		 * Wysyła tekst na strumień wyjściowy
		 * @param msg tekst, który ma być wysłany
		 * @return zwraca true, gdy wysyłka zakończyła się powodzeniem, w przeciwnym wypaku zwraca false
		 */
		boolean sendToClient(String msg) 
		{
			if(!socket.isConnected()) 
			{
				close();
				return false;
			}
			try 
			{
				outputStream.writeObject(msg);
			}
			catch(IOException ioe) 
			{
				showInfo("Nie udało się wysłać wiadomości do " + username);
				showInfo(" " + ioe);
				return false;
			}
			return true;
		}
		
		/**
		 * Uruchamia wątek serwera.
		 * Wątek dziłający do przerwania go przez LOGOUT
		 */
		public void run() 
		{
			boolean online = true;
			while(online) 
			{
				try 
				{
					message = (Message) inputStream.readObject();	//odczytywanie nadchodzących wiadomości
				}
				catch (IOException ioe) 
				{
					showInfo(username + " Wyjątek inputStream: " + ioe);
					break;				
				}
				catch(ClassNotFoundException cnfe) 
				{
					break;
				}
				String m = message.getMessage();	//odczytanie treści wiadomości
				switch(message.getType()) 				//odczytanie typu wiadomości
				{
					case Message.MESSAGE:
						sendMessage(username + ": " + m);
						break;
					case Message.CLIENTS_ONLINE:
						sendToClient("Zalogowani użytkownicy o " + time.format(new Date()) + ":\n");
						for(int i = 0; i < list.size(); ++i) 
						{
							ClientThread client = list.get(i);
							sendToClient((i+1) + ") " + client.username);
						}
						break;
					case Message.LOGOUT:
						online = false;
						showInfo("Użytkownik " + username + " rozłączył się.");
						break;
				}
			}
			remove(id);			//usunięcie siebie z listy klientów
			close();			//zamknięcie wątku
		}
		
		/**
		 * Metoda zamykająca wątek
		 */
		private void close() 
		{
			try 
			{
				if(outputStream != null) 
				{
					outputStream.close();
				}
			}
			catch(Exception e) {}
			try 
			{
				if(inputStream != null) 
				{
					inputStream.close();
				}
			}
			catch(Exception e) {};
			try 
			{
				if(socket != null) 
				{
					socket.close();
				}
			}
			catch (Exception e) {}
		}
	}
}