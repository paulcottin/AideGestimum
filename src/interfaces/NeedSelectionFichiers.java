package interfaces;

import java.io.File;
import java.util.ArrayList;

public interface NeedSelectionFichiers extends Runnable, LongTask {

	public void fichiersSelectionnes(ArrayList<File> files);
}
