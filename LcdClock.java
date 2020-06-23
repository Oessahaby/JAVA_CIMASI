package arduino;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.fazecast.jSerialComm.SerialPort;

import jSensors.Main;


public class LcdClock {
	
	static SerialPort chosenPort;

	public static void main(String[] args) {
		
		// create and configure the window
		JFrame window = new JFrame();
		window.setTitle("Arduino LCD Clock");
		window.setSize(400, 75);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// create a drop-down box and connect button, then place them at the top of the window
		final JComboBox<String> portList = new JComboBox<String>();
		final JButton connectButton = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(portList);
		topPanel.add(connectButton);
		window.add(topPanel, BorderLayout.NORTH);
		
		// populate the drop-down box
		SerialPort[] portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++)
			portList.addItem(portNames[i].getSystemPortName());
		
		// configure the connect button and use another thread to send data
		connectButton.addActionListener(new ActionListener(){ // Lorsqu'on click sur le button Connect 
			public void actionPerformed(ActionEvent arg0) {  // les fonction suivqntes sont executées 
				if(connectButton.getText().equals("Connect")) {
					// attempt to connect to the serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					if(chosenPort.openPort()) {
						connectButton.setText("Disconnect");
						portList.setEnabled(false);
						Thread thread = new Thread(){
							@Override public void run() {
								try {Thread.sleep(15000); } catch(Exception e) {}
								PrintWriter output = new PrintWriter(chosenPort.getOutputStream());
								while(true) {
									Main m = new Main();
									m.run();// Instanciation de fonction qui permet de recuperer la temperature
									output.print(Main.value);// Envoyer les données vers Proteus
									output.flush();
									try {Thread.sleep(15000); } catch(Exception e) {}
								}
							}
						};
						thread.start();
					}
				} else {
					chosenPort.closePort();
					portList.setEnabled(true);
					connectButton.setText("Connect");
				}
			}
		});
		window.setVisible(true);
		
	
	}

}
