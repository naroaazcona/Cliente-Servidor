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
    private String token;
	
	private JPanel pNorte,pCentro,pEste,pOeste;
	private JList<Reto> ListaRetos;

	
	private DefaultTableModel modeloTablaSesiones;
	private JTable tablaSesiones;
	private JScrollPane scrollTablaSesiones;
	
	private JLabel lblLogout;
	private JLabel lblTitulo,lblDeporte,lblDistancia,lblHoraInicio,lblHoraFin,lblDuracion;
	public SwingClientGUI(SwingClientController controller) {
			this.controller = controller;
			
			if(!login()) {
				System.exit(0);
		}
		
			 if (token != null) {
		            cargarMisRetos();
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
				JLabel label = new JLabel(value.nombre());
				label.setBackground(list.getBackground());
				label.setOpaque(true);
			
				if (isSelected) {
					label.setBackground(list.getSelectionBackground());
					label.setForeground(list.getSelectionForeground());
				}
				
				return label;
			});
			CargarRetos();
				
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
	
	 private boolean login() {
	        JTextField email = new JTextField(20);
	        email.setText(defaultEmail);
	        JPasswordField contrasenia = new JPasswordField(20);
	        contrasenia.setText(defaultPassword);

	        Object[] message = {
	            new JLabel("Introduce Email:"), email,
	            new JLabel("Introduce Contraseña:"), contrasenia
	        };

	        int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION);

	        if (option == JOptionPane.OK_OPTION) {
	            try {
	                // Actualizar el controlador para devolver el token
	                Boolean loginExitoso = controller.login(email.getText(), new String(contrasenia.getPassword()));
	                if (loginExitoso) {
	                    this.token = controller.getToken();  // Almacenar el token
	                    return true;
	                }
	                return false;
	            } catch (RuntimeException e) {
	                JOptionPane.showMessageDialog(this, e.getMessage());
	                return false;
	            }
	        }
	        return false;
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
	        // Verificar que tenemos el token
	        if (token == null) {
	            System.err.println("No hay token disponible para cargar los retos");
	            return;
	        }

	        System.out.println("Cargando mis retos con token: " + token);
	        List<Reto> misRetos = controller.getMisRetos(token);  // Pasar el token
	        
	        SwingUtilities.invokeLater(() -> {
	            ListaRetos.setListData(misRetos.toArray(new Reto[0]));
	            System.out.println("Retos cargados: " + misRetos.size());
	        });
	    } catch (RuntimeException e) {
	        System.err.println("Error al cargar mis retos: " + e.getMessage());
	        JOptionPane.showMessageDialog(this, "Error al cargar tus retos: " + e.getMessage());
	    }
	}
	private void CargarRetos() {
		try {
			List<Reto> retos = controller.getTodosRetos();

			SwingUtilities.invokeLater(() -> {
				ListaRetos.setListData(retos.toArray(new Reto[0]));
			});
		} catch (RuntimeException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	private void CargarSesiones(String token) {
	    System.out.println("Cargando sesiones con token: " + token);
	    	    try {
	    	        List<Sesion> sesiones = controller.getTodasSesiones(token);
	    	        System.out.println("Sesiones obtenidas: " + (sesiones != null ? sesiones.size(): null));
	        SwingUtilities.invokeLater(() -> {
	            // Limpiar la tabla actual
	            DefaultTableModel model = (DefaultTableModel) tablaSesiones.getModel();
	            model.setRowCount(0);

	            // Añadir cada sesión a la tabla
	            for (Sesion sesion : sesiones) {
	                model.addRow(new Object[]{
	                    sesion.id(),
	                    sesion.titulo(),
	                    sesion.deporte()
	                });
	            }
	            
	            // Refrescar la tabla
	            tablaSesiones.repaint();
	        });
	    } catch (RuntimeException e) {
	        System.err.println("Error al cargar sesiones: " + e.getMessage());

	        JOptionPane.showMessageDialog(this, e.getMessage());
	    }
	}
	
	//Cargar

	private void cargarSesionesXReto() {
		Reto retoSeleccionado = ListaRetos.getSelectedValue();
		System.out.println(retoSeleccionado);
		if (retoSeleccionado != null) {
			try {
				List<Sesion> sesiones = controller.getSesionesXReto(retoSeleccionado.id());

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
	        try {
	            // Obtener el ID de la sesión seleccionada
	            Object idObj = tablaSesiones.getValueAt(selectedRow, 0);
	            Long sesionId = null;
	            
	            // Convertir el ID al tipo correcto
	            if (idObj instanceof Long) {
	                sesionId = (Long) idObj;
	            } else if (idObj instanceof Integer) {
	                sesionId = ((Integer) idObj).longValue();
	            } else if (idObj instanceof String) {
	                sesionId = Long.parseLong((String) idObj);
	            }

	            if (sesionId != null) {
	                Sesion sesion = controller.getDetalleDeSesion(sesionId);
	                
	                // Actualizar la UI con valores seguros
	                SwingUtilities.invokeLater(() -> {
	                    // Usar getters seguros que manejan nulls
	                    lblTitulo.setText(sesion != null && sesion.titulo() != null ? sesion.titulo() : "N/A");
	                    lblDeporte.setText(sesion != null && sesion.deporte() != null ? sesion.deporte() : "N/A");
	                    lblDistancia.setText(sesion != null && sesion.distancia() != null ? 
	                        sesion.distancia().toString() + " km" : "N/A");
	                    lblHoraInicio.setText(sesion != null && sesion.horaInicio() != null ? 
	                        sesion.horaInicio().toString() : "N/A");
	                    lblHoraFin.setText(sesion != null && sesion.horaFin() != null ? 
	                        sesion.horaFin().toString() : "N/A");
	                    lblDuracion.setText(sesion != null && sesion.duracion() != null ? 
	                        sesion.duracion().toString() + " min" : "N/A");
	                });

	                // Añadir log para debug
	                System.out.println("Cargando detalles de sesión: " + sesionId);
	                if (sesion != null) {
	                    System.out.println("Sesión encontrada: " + sesion.titulo());
	                }
	            }
	        } catch (RuntimeException e) {
	            System.err.println("Error al cargar detalles de sesión: " + e.getMessage());
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this, 
	                "Error al cargar detalles de la sesión: " + e.getMessage(), 
	                "Error", 
	                JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
	

	 public String getToken() {
	        return token;
	    }

	    public void setToken(String token) {
	        this.token = token;
	    }
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new SwingClientGUI(new SwingClientController()));
	}
	
	
}