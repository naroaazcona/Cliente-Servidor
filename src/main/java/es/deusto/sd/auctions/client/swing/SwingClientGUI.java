/**
 * This code is based on solutions provided by Claude Sonnet 3.5 and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package es.deusto.sd.auctions.client.swing;

import java.awt.BorderLayout;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;


import es.deusto.sd.auctions.client.data.Reto;
import es.deusto.sd.auctions.client.data.Sesion;



/**
 * SwingClientGUI class is a Swing-based client that demonstrates the usage of the
 * AuctionsService. It is implemented using a classic Controller pattern.
 */
public class SwingClientGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	// Controller instance
	private final SwingClientController controller;

	// Default login credentials
	private String defaultEmail = "gorka.ortuzar@gmail.com";
	private String defaultPassword = "1";
	
	private JPanel pNorte,pCentro,pEste,pOeste;
	private JList<Reto> ListaRetos;
	
	private DefaultTableModel modeloTablaSesiones;
	private JTable tablaSesiones;
	private JScrollPane scrollTablaSesiones;
	
	private JLabel lblLogout;
	private JLabel lblTitulo,lblDeporte,lblDistancia,lblHoraInicio,lblHoraFin,lblDuracion;
	public SwingClientGUI(SwingClientController controller) {
			this.controller = controller;
			
			if(!Login()) {
				System.exit(0);
		}
		
			
			pNorte = new JPanel();
			//pNorte.setBackground(Color.GREEN);
			pCentro = new JPanel();
			//pCentro.setBackground(Color.BLUE);
			pEste = new JPanel();
			//pEste.setBackground(Color.YELLOW);
			pOeste = new JPanel();
			//pOeste.setBackground(Color.RED);
			
			getContentPane().add(pNorte, BorderLayout.NORTH);
			getContentPane().add(pCentro, BorderLayout.CENTER);
			getContentPane().add(pEste, BorderLayout.EAST);
			getContentPane().add(pOeste, BorderLayout.WEST);
			
			
			//lblLogout
			lblLogout = new JLabel("Logout", SwingConstants.RIGHT);
			lblLogout.setForeground(Color.BLUE);
			lblLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			lblLogout.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent evt) {
					performLogout();
				}
			});
			pNorte.add(lblLogout);
			
			
			//JList de Retos
			ListaRetos = new JList<>();
			
			ListaRetos.addListSelectionListener((e)->{
				if(!e.getValueIsAdjusting()) {
					cargarSesionesXReto();
				}
			});
			
			ListaRetos.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
				JLabel label = new JLabel(value.NombreReto());
				label.setBackground(list.getBackground());
				label.setOpaque(true);
			
				if (isSelected) {
					label.setBackground(list.getSelectionBackground());
					label.setForeground(list.getSelectionForeground());
				}
				
				return label;
			});
			cargarMisRetos();
				
			JScrollPane ListaRetosScrollPane = new JScrollPane(ListaRetos);
			ListaRetosScrollPane.setBorder(new TitledBorder("Retos"));
			pOeste.add(ListaRetosScrollPane);
			
			
			
			
			
			//JTable de sesiones
			
			String[] columnNames = {"Id", "Titulo", "Deporte"};
			 modeloTablaSesiones = new DefaultTableModel(columnNames, 0) {
		            @Override
		            public boolean isCellEditable(int row, int column) {
		                return false; 
		            }
		        };
			tablaSesiones = new JTable(modeloTablaSesiones);
			tablaSesiones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tablaSesiones.getSelectionModel().addListSelectionListener(e -> {
				if (!e.getValueIsAdjusting()) {
					cargarDetallesSesion();
				}
			});
			scrollTablaSesiones = new JScrollPane(tablaSesiones);
			scrollTablaSesiones.setBorder(new TitledBorder("Sesiones del reto"));

			pCentro.add(scrollTablaSesiones);
			
			//detalles de la sesiones
			
			JPanel jPanelDetalleSesion = new JPanel(new GridLayout(6, 2, 10, 10));
			jPanelDetalleSesion.setBorder(new TitledBorder("Detalles de la sesion"));

			jPanelDetalleSesion.add(new JLabel("Titulo:"));
			lblTitulo = new JLabel();
			jPanelDetalleSesion.add(lblTitulo);

			jPanelDetalleSesion.add(new JLabel("Deporte:"));
			lblDeporte = new JLabel();
			jPanelDetalleSesion.add(lblDeporte);

			jPanelDetalleSesion.add(new JLabel("Distancia:"));
			lblDistancia = new JLabel();
			jPanelDetalleSesion.add(lblDistancia);

			jPanelDetalleSesion.add(new JLabel("Hora Inicio:"));
			lblHoraInicio = new JLabel();
			jPanelDetalleSesion.add(lblHoraInicio);

			jPanelDetalleSesion.add(new JLabel("Hora Fin:"));
			lblHoraFin = new JLabel();
			jPanelDetalleSesion.add(lblHoraFin);
			
			jPanelDetalleSesion.add(new JLabel("Duracion:"));
			lblDuracion = new JLabel();
			jPanelDetalleSesion.add(lblDuracion);
			
			
			pEste.add(jPanelDetalleSesion);
			
			
			
			
			

			
			
			
			setSize(1024,400);
	        setResizable(false);
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle("Auction cliente");
			setVisible(true);
	
	}
	
	private boolean Login() {
		JTextField email = new JTextField(20);
		email.setText(defaultEmail);
		JPasswordField contrasenia = new JPasswordField(20);
		contrasenia.setText(defaultPassword);

		Object[] message = { new JLabel("Introduce Email:"), email, new JLabel("Introduce Contraseña:"), contrasenia};

		int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION);

		if (option == JOptionPane.OK_OPTION) {
			try {
				return controller.login(email.getText(), new String(contrasenia.getPassword()));
			} catch (RuntimeException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}

	private void performLogout() {
		try {
			controller.logout();
			JOptionPane.showMessageDialog(this, "Cierre de sesión exitoso..");
			System.exit(0);
		} catch (RuntimeException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	private void cargarMisRetos() {
	    try {
	        List<Reto> misRetos = controller.getMisRetos();
	        SwingUtilities.invokeLater(() -> {
	            ListaRetos.setListData(misRetos.toArray(new Reto[0]));
	        });
	    } catch (RuntimeException e) {
	        JOptionPane.showMessageDialog(this, e.getMessage());
	    }
	}
	
	private void CargarRetos() {
		try {
			List<Reto> categories = controller.getTodosRetos();

			SwingUtilities.invokeLater(() -> {
				ListaRetos.setListData(categories.toArray(new Reto[0]));
			});
		} catch (RuntimeException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	//Cargar

	private void cargarSesionesXReto() {
		Reto retoSeleccionado = ListaRetos.getSelectedValue();

		if (retoSeleccionado != null) {
			try {
				List<Sesion> sesiones = controller.getSesionesXReto(retoSeleccionado.NombreReto());

				SwingUtilities.invokeLater(() -> {
					DefaultTableModel model = (DefaultTableModel) tablaSesiones.getModel();
					model.setRowCount(0);

					for (Sesion sesion : sesiones ) {
						model.addRow(new Object[] { sesion.id(), sesion.titulo(),sesion.deporte()});
					}
				});
			} catch (RuntimeException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}
//	
	private void cargarDetallesSesion() {
		int selectedRow = tablaSesiones.getSelectedRow();

		if (selectedRow != -1) {
			Long sesionId = (Long) tablaSesiones.getValueAt(selectedRow, 0);

			try {
				Sesion sesion = controller.getDetalleDeSesion(sesionId);

				SwingUtilities.invokeLater(() -> {
					lblTitulo.setText(sesion.titulo());
					lblDeporte.setText(sesion.deporte());
					lblDistancia.setText(sesion.duracion().toString());
					lblHoraInicio.setText(sesion.horaInicio().toString());
					lblHoraFin.setText(sesion.horaFin().toString());
					lblDuracion.setText(sesion.distancia().toString());
					
				});
			} catch (RuntimeException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new SwingClientGUI(new SwingClientController()));
	}
	
	
}