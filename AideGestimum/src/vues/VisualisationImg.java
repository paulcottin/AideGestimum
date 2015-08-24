package vues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import exceptions.ParametrageError;
import main.Principale;
import utilitaires.WatchDir;

public class VisualisationImg extends JFrame implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<File> list;
	private File imgFile, dossierEcoute;
	private int cpt;
	private JLabel nom, cptLabel, imagesTraitees, watchDirMessage;
	private JButton prec, suiv, passer, rafraichir, invalider, oublier, copier;
	private JPanel contentPane, gauche;
	private Vector<Vector<String>> vec;
	private DefaultTableModel model;
	private Vector<String> columnName;
	private JTable table;
	private JScrollPane listPanel;
	private ImageVisualisation img;
	private ArrayList<String> fichiersTraites, fichiersANePasTraiter;
	private DecimalFormat dFormat;
	private WatchDir watchDir;
	private Thread watchThread;

	public VisualisationImg(ArrayList<File> list, int cpt) {
		super("Visualisateur d'images");
		this.fichiersTraites = new ArrayList<String>();
		this.fichiersANePasTraiter = new ArrayList<String>();
		this.list = new ArrayList<File>();
		this.list.addAll(list);
		this.imgFile = list.get(cpt);
		this.cpt = cpt;
		this.dossierEcoute = null;
		try {
			this.watchDir = new WatchDir(this, null, false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.watchDirMessage = new JLabel();
		this.dFormat = new DecimalFormat();
		dFormat.setMaximumFractionDigits(2);
		this.setSize(1300, 900);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		img = null;
		try {
			img = new ImageVisualisation(ImageIO.read(imgFile.getAbsoluteFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		nom = new JLabel(imgFile.getPath()+"   || (copié dans le presse papier) : numéro "+cpt);
		JPanel nomP = new JPanel();
		nomP.add(nom);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(img, BorderLayout.CENTER);
		contentPane.add(nomP, BorderLayout.NORTH);
		initButton();
		initList();
		this.setContentPane(contentPane);
		this.setJMenuBar(new MenuVisualisation());
		this.setVisible(true);

		copyToClipboard();
	}

	public void refrech() {
		watchDirMessage.setText(" ");
		imgFile = list.get(cpt);
		try {
			try {
				img.setImg(ImageIO.read(imgFile.getAbsoluteFile()));
			} catch (IIOException e1) {
				throw new ParametrageError("Impossible de lire l'image \""+imgFile.getAbsolutePath()+"\"");
			}
		}
		catch (ParametrageError e1) {
			e1.printMessage();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		nom.setText("<html>"+imgFile.getPath()+
				"<br/>Longueur x Largeur : "+img.getWidth()+" x "+img.getHeight()+"<html>");
		cptLabel.setText(" "+cpt+" ");
		double pct = ((new Integer(fichiersTraites.size()+fichiersANePasTraiter.size())).doubleValue()/((new Integer(list.size()).doubleValue())))*100;
		imagesTraitees.setText("Images traitées : "+(fichiersTraites.size()+fichiersANePasTraiter.size())+"/"+list.size()+" ("+dFormat.format(pct)+"%)");
		copyToClipboard();
		updateList();
		table.setRowSelectionInterval(cpt, cpt);
		table.requestFocus();
		this.revalidate();
	}

	private void initButton() {
		prec = new JButton("Précédent");
		suiv = new JButton("Valider");
		passer = new JButton("Suivant");
		rafraichir = new JButton("Rafraichir");
		invalider = new JButton("Invalider");
		oublier = new JButton("Oublier");
		copier = new JButton("Copier");
		cptLabel = new JLabel(" "+cpt+" ");

		suiv.setBackground(Color.GREEN);
		invalider.setBackground(Color.RED);

		prec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mouvements(-1);
			}
		});
		suiv.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkImgCourant();
				mouvements(1);
			}
		});
		suiv.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				checkImgCourant();
				mouvements(1);
			}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {}
		});
		passer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mouvements(1);
			}
		});
		rafraichir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refrech();
			}
		});
		invalider.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				invalider();
			}
		});
		oublier.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				oublier();
				mouvements(1);
			}
		});
		copier.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				copier();
			}
		});

		copier.setVisible(false);
		JPanel buttons = new JPanel();
		buttons.add(invalider);
		buttons.add(prec);
		buttons.add(cptLabel);
		buttons.add(passer);
		buttons.add(suiv);
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		south.add(buttons, BorderLayout.CENTER);
		south.add(watchDirMessage, BorderLayout.SOUTH);
		contentPane.add(south, BorderLayout.SOUTH);
	}

	private void initList() {
		gauche = new JPanel();
		gauche.setLayout(new BoxLayout(gauche, BoxLayout.PAGE_AXIS));
		listPanel = new JScrollPane();
		listPanel.setPreferredSize(new Dimension(150, getHeight()));
		columnName = new Vector<String>();
		columnName.add("Fichiers");
		columnName.add("Traitées");
		vec = new Vector<Vector<String>>();
		model = new DefaultTableModel(vec, columnName);
		table = new JTable(model);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		for (KeyListener listener : table.getKeyListeners()) table.removeKeyListener(listener);
		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {
				cpt = table.getSelectedRow();
				refrech();
			}
		});
		table.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 38 || e.getKeyChar() == 37)
					mouvements(-1);
				else if (e.getKeyCode() == 39 || e.getKeyCode() == 40)
					mouvements(1);
			}
		});
		listPanel.setViewportView(table);
		updateList();

		imagesTraitees = new JLabel();

		gauche.add(imagesTraitees);
		gauche.add(listPanel);
		JPanel buttons = new JPanel();
		buttons.add(oublier);
		buttons.add(rafraichir);
		gauche.add(buttons);
		gauche.add(copier);
		contentPane.add(gauche, BorderLayout.WEST);
	}

	private void updateList() {
		vec = new Vector<Vector<String>>();
		model.setRowCount(0);
		Vector<String> v;
		for (int i = 0; i < list.size(); i++) {
			v = new Vector<String>();
			v.addElement(list.get(i).getName());
			if (fichiersTraites.contains(list.get(i).getAbsolutePath())) {
				v.addElement("OK");
			}
			else if (fichiersANePasTraiter.contains(list.get(i).getAbsolutePath())) {
				v.add("NON");
			}
			vec.add(v);
		}

		model.setDataVector(vec, columnName);
		table.setModel(model);

		TableColumnModel cs = table.getColumnModel();
		TableColumn c1 = (TableColumn) cs.getColumn(1);
		((TableColumn) c1).setMinWidth(50);
		((TableColumn) c1).setMaxWidth(50);

		listPanel.setViewportView(table);
	}

	private void mouvements(int plusOuMoins) {
		if (plusOuMoins > 0)
			cpt++;
		else
			cpt--;
		if (cpt >= list.size())
			cpt = 0;
		else if (cpt < 0)
			cpt = list.size() -1 ;
		imgFile = list.get(cpt);
		refrech();
	}

	private void invalider() {
		if (fichiersTraites.contains(imgFile.getAbsolutePath())) {
			int index = fichiersTraites.indexOf(imgFile.getAbsolutePath());
			fichiersTraites.remove(index);
			refrech();
		}
		if (fichiersANePasTraiter.contains(imgFile.getAbsolutePath())) {
			int index = fichiersANePasTraiter.indexOf(imgFile.getAbsolutePath());
			fichiersANePasTraiter.remove(index);
			refrech();
		}

	}

	private void oublier() {
		invalider();
		if (!fichiersANePasTraiter.contains(imgFile.getAbsolutePath())) {
			fichiersANePasTraiter.add(imgFile.getAbsolutePath());
		}
	}

	private void copyToClipboard() {
		StringSelection contents = new StringSelection(imgFile.getAbsolutePath());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(contents, null);
	}

	private void checkImgCourant() {
		invalider();
		if (!fichiersTraites.contains(imgFile.getAbsolutePath())) {
			fichiersTraites.add(imgFile.getAbsolutePath());
		}
	}

	private void copier() {
		File to = new File(dossierEcoute.getAbsolutePath()+"\\"+imgFile.getName());
		try {
			Principale.fileCopy(imgFile, to);
		} catch (ParametrageError e) {
			e.printMessage();
		}
	}

	class MenuVisualisation extends JMenuBar {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JMenu fichier, options;
		private JMenuItem chargerFichier, sauvegarder, ecouteDossier;

		public MenuVisualisation() {
			fichier = new JMenu("Fichier");
			sauvegarder = new JMenuItem("Sauvegarder le travail");
			chargerFichier = new JMenuItem("Charger une sauvegarde");
			options = new JMenu("Options");
			ecouteDossier = new JMenuItem("Activer/Désactiver l'écoute d'un dossier");

			sauvegarder.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					sauvegarder();
				}
			});

			chargerFichier.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
					File f = null;
					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
						f = fileChooser.getSelectedFile();
					if (f == null) return;
					charger(f);
				}
			});

			ecouteDossier.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					copier.setVisible(!copier.isVisible());
					if (copier.isVisible())
						try {
							activerEcoute();
						} catch (ParametrageError e1) {
							e1.printMessage();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					else
						desactiverEcoute();
				}
			});

			fichier.add(sauvegarder);
			fichier.addSeparator();
			fichier.add(chargerFichier);
			options.add(ecouteDossier);
			this.add(fichier);
			this.add(options);
		}

		@SuppressWarnings("deprecation")
		private void sauvegarder() {
			File svg = null;
			try {
				svg = new File(
						"svg_ImprimEcran_"+Principale.topics.getName()
						+"_"+GregorianCalendar.getInstance().getTime().toGMTString().replaceAll("( |:)", "_"));
				BufferedWriter bw = new BufferedWriter(new FileWriter(svg));

				for (String string : fichiersTraites) {
					bw.write(string+"\r\n");
				}
				bw.write("not\r\n");
				for (String string : fichiersANePasTraiter) {
					bw.write(string+"\r\n");
				}
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Principale.messageFin("Sauvegarde effectuée dans le fichier <br/>"+svg.getAbsolutePath());
		}

		private void charger(File f) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String ligne = "";
				boolean ok = true;

				while ((ligne = br.readLine()) != null) {
					if (ligne.matches("^not"))
						ok = false;
					if (ok)
						fichiersTraites.add(ligne);
					else 
						fichiersANePasTraiter.add(ligne);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			refrech();
		}

		private void activerEcoute() throws ParametrageError, IOException {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			File f = null;
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				f = fileChooser.getSelectedFile();
			if (f == null) throw new ParametrageError("Il faut choisir un dossier !");

			dossierEcoute = f;
			watchDir.setDir(Paths.get(f.getAbsolutePath()));
			watchThread = new Thread(watchDir);
			watchThread.start();
		}

		private void desactiverEcoute() {
			watchThread.interrupt();
		}
	}

	class ImageVisualisation extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Image img;
		private int width = 1070, height = 600;

		public ImageVisualisation(Image img) {
			this.img = img;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img.getWidth(null) > width)
				g.drawImage(img, 0, 0, width, height, this);
			else
				g.drawImage(img, 0, 0, this);
		}

		public void setImg(Image img) {
			this.img = img;
			this.repaint();
			this.revalidate();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		watchDirMessage.setText(watchDir.getMessage());
	}

}
