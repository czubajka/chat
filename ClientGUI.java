//ClientGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** Klasa definiująca wygląd i działanie GUI programu klienckiego komunikatora sieciowego.
 * Uruchamiana z klasy StartClient
 *@author Karolina Nędza-Sikoniowska
 *@version 1.0
 */
public class ClientGUI extends JFrame implements ActionListener 
{
	/** referencja do instancji klasy Client obsługującej komunikator */
	private Client client;
	/** status połączenia */
	private boolean online;
	/** domyślny numer portu */
	private int defaultPort;
	/** domyślny adres hosta (serwera) */
	private String defaultHost;
	
	/** opis pola "Nazwa użytkownika" i "Napisz wiadomość " */
	private JLabel label;
	/** okno rozmów */
	private JTextArea talkArea;
	/** do wprowadzania nazwy użytkownika i wiadomości */
	private JTextField sendField;
	/** do wprowadzania adresu serwera */
	private JTextField serverField;
	/** do wprowadzania numeru portu */
	private JTextField portField;
	/** przycisk do zalogowania się na serwerze */
	private JButton login;
	/** przycisk do wylogowania się z serwera */
	private JButton logout;
	/** przycisk do wyświetlenia zalogowanych klientów */
	private JButton clientsOnline;
	
	/** Konstruktor klasy.
	 * @param host adres hosta (serwera) - domyślnie "localhost"
	 * @param port numer portu 
	 */
	ClientGUI(String host, int port) 
	{
		super("CLIENT by KarolinaNS");
		defaultHost = host;
		defaultPort = port;
		
		serverField = new JTextField(host);
		portField = new JTextField("" + port);
		serverField.setHorizontalAlignment(SwingConstants.CENTER);
		portField.setHorizontalAlignment(SwingConstants.CENTER);
		sendField = new JTextField("<Anonim>");
		talkArea = new JTextArea("...", 30, 10);
		login = new JButton("WEJDŹ");
		logout = new JButton("WYJDŹ");
		clientsOnline = new JButton("Użytkownicy ONLINE");
		label = new JLabel("Podaj nick: ");
		
		JPanel north = new JPanel(new GridLayout(2,1));
		JPanel center = new JPanel(new GridLayout(1,1));
		JPanel south = new JPanel(new GridLayout(5,1));
		
		north.add(new JLabel("Serwer : "));
		north.add(serverField);
		north.add(new JLabel("Port : "));
		north.add(portField);

		south.add(login);
		south.add(logout);
		south.add(clientsOnline);
		south.add(label);
		south.add(sendField);
		center.add(new JScrollPane(talkArea));
		
		getContentPane().add(north, BorderLayout.NORTH);
		getContentPane().add(center, BorderLayout.CENTER);
		getContentPane().add(south, BorderLayout.SOUTH);
		
		sendField.setBackground(Color.WHITE);
		talkArea.setEditable(false);
		talkArea.setLineWrap(true);
		talkArea.setWrapStyleWord(true);
		login.addActionListener(this);
		logout.addActionListener(this);
		logout.setEnabled(false);
		clientsOnline.addActionListener(this);
		clientsOnline.setEnabled(false);

		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 500);
		setVisible(true);
		sendField.requestFocus();
	}
	
	/**
	 * Metoda wywoływana przy nieudanym połączeniu w celu przywrócenia pól i przycisków do stanu sprzed
	 * połączenia
	 */
	void reset() 
	{
		online = false;
		label.setText("Podaj nick: ");
		sendField.setText("Anonymous");
		login.setEnabled(true);
		logout.setEnabled(false);
		clientsOnline.setEnabled(false);
		portField.setText("" + defaultPort);
		serverField.setText(defaultHost);
		serverField.setEditable(true);
		portField.setEditable(true);
		sendField.removeActionListener(this);	//aby pole username nie reagowało na enter
	}
	
	/**
	 * Wysyła wiadomość do talkArea
	 * @param s tekts wysyłany do talkArea,
	  */ 
	void showInTalkArea(String s) 
	{
		talkArea.append(s + "\n");
		talkArea.setCaretPosition(talkArea.getText().length() - 1);
	}
	
	/**
	* Metoda przechwytująca zdarzenia i definiująca reakcję na zdarzenie
	* @param event zdarzenie mające być obsłużone
	*/
	public void actionPerformed(ActionEvent event) 
	{
		Object object = event.getSource();
		if(object == login) 						//rządanie połączenia
		{
			String server = serverField.getText().trim();
			if(server.length() == 0)
			{
				return;
			}
			String portNumber = portField.getText().trim();
			if(portNumber.length() == 0)
			{
				return;
			}
			String username = sendField.getText().trim();
			if(username.length() == 0)
			{
				return;
			}
			
			int port = 0;
			try 
			{
				port = Integer.parseInt(portNumber);
			}
			catch(Exception e) 
			{
				return;
			}

			client = new Client(server, port, username, this);		//tworzenie nowego klienta
			if (!client.start())
				{
					return;
				}
			online = true;
			serverField.setEditable(false);
			portField.setEditable(false);			
			
			login.setEnabled(false);
			logout.setEnabled(true);
			clientsOnline.setEnabled(true);
			label.setText("Pisz: ");
			sendField.setText("");
			sendField.addActionListener(this);			//włączenie listenera do wysyłania wiadomości enterem
		}
		else if(object == clientsOnline) 
		{
			client.sendMessage(new Message(Message.CLIENTS_ONLINE, ""));				
			return;
		}
		else if(object == logout) 
		{
			client.sendMessage(new Message(Message.LOGOUT, ""));
			return;
		}
		else if(online) 
		{
			client.sendMessage(new Message(Message.MESSAGE, sendField.getText()));				
			sendField.setText("");
			return;
		}
	}
}
