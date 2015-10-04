package si.comptus.jrelex.container;

import java.io.Serializable;

public class CPosition implements Serializable{
	private int position;
	private int maxPositionStringLength;
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getMaxPositionStringLength() {
		return maxPositionStringLength;
	}
	public void setMaxPositionStringLength(int maxPositionStringLength) {
		this.maxPositionStringLength = maxPositionStringLength;
	}
	
	
}
