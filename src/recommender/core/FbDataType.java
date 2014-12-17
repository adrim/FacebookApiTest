/**
 * 
 */
package recommender.core;

/**
 * @author Adriana
 *
 */
public enum FbDataType {
	LIKE(1),
	LINK(2),
	INTEREST(3);
	
	private int value;
	
	private FbDataType(int value) {
		this.value = value;
	}
	public int getValue() {
		return this.value;
	}
	public static boolean contains(int value) {
		FbDataType[] vals = FbDataType.values();
		for (FbDataType val : vals) {
			if (val.getValue() == value)
				return true;
		}
		return false;
	}
}
