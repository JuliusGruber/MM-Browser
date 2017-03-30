
public class Sample {
	
	private String  filePath;
	private String fileName;
	private double xPosition;
	private double yPosition;
	private int polyAdress;
	private boolean isInBasket;
	private String shape;
	
	
	public Sample(String filePath, String fileName,double xPosition, double yPosition){
		this.filePath = filePath;
		this.fileName = fileName;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}
	
	public Sample(String filePath, String fileName,double xPosition, double yPosition, int polyAdress){
		this.filePath = filePath;
		this.fileName = fileName;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.polyAdress = polyAdress;
	}
	
	public Sample(String filePath, String fileName,double xPosition, double yPosition, int polyAdress, boolean isInBasket){
		this.filePath = filePath;
		this.fileName = fileName;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.polyAdress = polyAdress;
		this.isInBasket = isInBasket;
	}

	public Sample(String filePath, String fileName,double xPosition, double yPosition, int polyAdress, boolean isInBasket, String shape){
		this.filePath = filePath;
		this.fileName = fileName;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.polyAdress = polyAdress;
		this.isInBasket = isInBasket;
		this.shape = shape;
	}
	
	public Sample( int polyAdress, String filePath ){
		this.filePath = filePath;
		this.polyAdress = polyAdress;
	}
	
	public Sample(String filePath){
		this.filePath = filePath;
		
	}
	
	public Sample(){
		this.filePath = null;
		this.fileName = null;
		this.xPosition = 0;
		this.yPosition = 0;
	}
	

	
	public Sample(String filePath, String shape) {
		this.filePath = filePath;
		this.shape = shape;
	}

	@Override
	public int hashCode() {
		return filePath.hashCode();
	}



	@Override
	public boolean equals(Object aSample) {
		Sample s = (Sample)aSample;
		return getFilePath().equals(s.getFilePath());
	}




	public String toString(){
		String sampleString = fileName+" with position: "+xPosition+" "+yPosition;
		
		return sampleString;
		
	}
	
	//get and set Methods
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public double getxPosition() {
		return xPosition;
	}
	public void setxPosition(double xPosition2) {
		this.xPosition = xPosition2;
	}
//	public double getyPostion() {
//		return yPosition;
//	}
	public void setyPostion(double yPosition) {
		this.yPosition = yPosition;
	}

	public int getPolyAdress() {
		return polyAdress;
	}

	public void setPolyAdress(int polyAdress) {
		this.polyAdress = polyAdress;
	}

	public boolean getIsInBasket() {
		return isInBasket;
	}

	public void setInBasket(boolean isInBasket) {
		this.isInBasket = isInBasket;
	}

	public double getyPosition() {
		return yPosition;
	}

	public void setyPosition(double yPosition) {
		this.yPosition = yPosition;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}
	
	

}
