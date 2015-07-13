package interfaces;

import java.io.File;
import java.util.ArrayList;

public interface LancerAction extends Runnable, LongTask, NeedSelectionFichiers {

	public void lancerActionAll();
	public void lancerAction(ArrayList<File> files);
	public void parametrer();
}
