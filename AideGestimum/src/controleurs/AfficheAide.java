package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import exceptions.AideException;
import vues.Aide;

public class AfficheAide implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			new Aide();
		} catch (AideException e) {
			e.printMessage();
		}
	}

}
