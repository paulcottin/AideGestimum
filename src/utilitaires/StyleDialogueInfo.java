package utilitaires;

public class StyleDialogueInfo {

	private String mot, style;

	  public StyleDialogueInfo() {}

	  public StyleDialogueInfo(String mot, String style){
	    this.mot = mot;
	    this.style = style;
	  }

	public String getMot() {
		return mot;
	}

	public void setMot(String mot) {
		this.mot = mot;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
}
