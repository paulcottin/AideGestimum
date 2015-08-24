package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import utilitaires.Utils;

public class NoHelp implements ActionListener{
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		(new Utils()).checkNoHelpWindows();
	}

}
