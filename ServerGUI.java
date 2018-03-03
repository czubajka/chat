//ServerGUI.java
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Klasa definiująca wygląd i działanie GUI programu serwerowego komunikatora sieciowego.
 * Uruchamiana z klasy StartServer
 *@author Karolina Nędza-Sikoniowska
 *@version 1.0
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener 
{
	/** obiekt typu Serwer*/
	private Server server;
	/** przycisk włączający lub wyłączający serwer */
	private JButton startStop;
	/** pola typu JTextArea do monitorowania zdarzeń*/
	private JTextArea eventsArea;
	/** pola typu JTextArea do monitorowania rozmowy*/
	private JTextArea talkArea;
	/** pole do wprowadzania numeru portu*/
	private JTextField portNumberField;
	
	/** Konstruktor klasy.
	 * @param port numer portu nasłuchiwania dla serwera 
	 */
	ServerGUI(int port) 
	{
		super("SERWER by KarolinaNS");
		server = null;
		
		JPanel north = new JPanel(new GridLayout(1, 2));
		north.add(new JLabel("Okno wydarzen serwera: "));
		north.add(new JLabel("Okno rozmow: "));
		getContentPane().add(north, BorderLayout.NORTH);
		
		JPanel center = new JPanel(new GridLayout(1, 2));
		eventsArea = new JTextArea(10,30);
		eventsArea.setEditable(false);
		eventsArea.setLineWrap(true);
		eventsArea.setWrapStyleWord(true);
		showInEventsArea("...\n");
		center.add(new JScrollPane(eventsArea));
		
		talkArea = new JTextArea(10,30);
		talkArea.setEditable(false);
		talkArea.setLineWrap(true);
		talkArea.setWrapStyleWord(true);
		showInTalkArea("...\n");
		center.add(new JScrollPane(talkArea));
		getContentPane().add(center);
		
		JPanel south = new JPanel();
		south.add(new JLabel("Numer portu: "));
		portNumberField = new JTextField("" + port);	//ustawienie domy�lnego portu
		south.add(portNumberField);
		startStop = new JButton("START");
		south.add(startStop);
		startStop.addActionListener(this);	//nasłuchiwanie zdarzeń przycisku START/STOP
		getContentPane().add(south, BorderLayout.SOUTH);
		
		addWindowListener(this);		//nasłuchiwanie zdarzenia z okna
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setSize(800, 300);
		setVisible(true);	
	}		

	/**
	 * Umieszcza napis w oknie talkArea,
	 * karetkę na koniec okna.
	 * @param s napis, który ma być umieszczony
	 */
	void showInTalkArea(String s) 
	{
		talkArea.append(s);
		talkArea.setCaretPosition(talkArea.getText().length() - 1);
	}
	
	/**
	 * Umieszcza napis w oknie eventsArea,
	 * karetkę na koniec okna.
	 * @param s napis, który ma być umieszczony
	 */
	void showInEventsArea(String s) 
	{
		eventsArea.append(s);
		eventsArea.setCaretPosition(eventsArea.getText().length() - 1);
	}
	
	/**
	 * Obsługa wydarzenia WindowEvent zamykająca okno
	 * @param event zdarzenie, które ma być obsłużone.
	 */
	public void windowClosing(WindowEvent event) 
	{
		// sprawdzanie, czy serwer został uruchomiony
		if(server != null) 
		{
			try 
			{
				server.stop();
			}
			catch(Exception e) {}
			server = null;
		}
		//niszczy okno
		dispose();
		System.exit(0);
	}
	
	// pozostałe metody WindowListener niezdefiniowane
	public void windowClosed(WindowEvent event) {}
	public void windowOpened(WindowEvent event) {}
	public void windowIconified(WindowEvent event) {}
	public void windowDeiconified(WindowEvent event) {}
	public void windowActivated(WindowEvent event) {}
	public void windowDeactivated(WindowEvent event) {}
	
	/**
	 * Metoda reagująca na kliknięcie przycisku START/STOP
	 * @param event przechwycane zdarzenie przycisku
	 */
	public void actionPerformed(ActionEvent event) 
	{
		
		// jeżeli kliknięto STOP
		if(server != null) 
		{
			server.stop();
			server = null;
			portNumberField.setEditable(true);
			startStop.setText("START");
		}
		// jeśli kliknito START
		else
		{
			int port = 0;	//zmienna do wczytania portu
			try 
			{
				port = Integer.parseInt(portNumberField.getText().trim());
			}
			catch(Exception e) 
			{
				showInEventsArea("Nieprawidlowy numer portu. Podaj inny. ");
				return;
			}
	
			server = new Server(port, this);
			// uruchomienie wątku serwera
			new RunServer().start();
			startStop.setText("STOP");
			portNumberField.setEditable(false);
		}
	}

	/**
	 * Klasa dziedzicząca po Thread.
	 * Uruchamia i sprząta po zakończeniu pracy serwera.
	 */	
	class RunServer extends Thread 
	{
	    /**
	    * Metoda run() uruchamia wątek serwera. Serwer działa do zatrzymania lub do wystąpienia błedu.
	    * Po zatrzymaniu serwera następuje zamiana opisu przycisku stopStart na "start",
	    * ustawienie edytowalności pola numeru portu na "true" oraz wysyłany jest komunikat o zatrzymaniu.*/
		public void run() 
		{
			server.start();	//działa do zatrzymania  
			showInEventsArea("Serwer zatrzymany.\n");
			server = null;
			startStop.setText("START");
			portNumberField.setEditable(true);
		}
	}
}
