package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import main.Principale;

public class ExporterCSV implements ActionListener{

	Principale p;
	
	public ExporterCSV(Principale p) {
		this.p = p;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			p.exportCSV();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
