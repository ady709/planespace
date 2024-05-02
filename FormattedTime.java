package orbit;

public class FormattedTime {

	private int secInY = (int) (60*60*24*365.25);
	private int secInD = 60*60*24;
	private int secInH = 60*60;
	private int secInM = 60;
	
	public int years;
	public int days;
	public int hours;
	public int minutes;
	public int seconds;

	public String y;
	public String d;
	public String h;
	public String m;
	public String s;
	
	public String timeText;
	
	public String timeText2;
	private String y2, d2, h2, m2, s2;
	private String y2u, d2u, h2u, m2u, s2u;
	
	public FormattedTime(double time) {
		
		years = (int)Math.floor(time/secInY);
		days = (int)(long)Math.floor((time-years*secInY)/secInD);
		
		hours = (int)Math.floor((time-years*secInY-days*secInD)/secInH);
		minutes = (int)Math.floor((time-years*secInY-days*secInD-hours*secInH)/secInM);
		seconds = (int)Math.floor((time-years*secInY-days*secInD-hours*secInH-minutes*secInM));

		y = String.valueOf(years);
		d = String.format("%03d",days);
		h = String.format("%02d", hours);
		m = String.format("%02d",minutes);
		s = String.format("%02d",seconds);
		
		timeText = y + " Y " + d + " d / " + h + ":" + m + ":" + s;
		
		if(years>0) {y2=y; if(years==1){y2u=" year ";}else{y2u=" years ";}}else {y2="";y2u="";}

		if(days>0) {d2=String.valueOf(days); if(days==1){d2u=" day ";}else{d2u=" days ";}} else {d2="";d2u="";}
		
		if(hours>0) {h2=String.valueOf(hours); if(hours==1){h2u=" hour ";}else{h2u=" hours ";}} else {h2="";h2u="";}
		if(minutes>0) {m2=String.valueOf(minutes); if(minutes==1){m2u=" minute ";}else{m2u=" minutes ";}} else {m2="";m2u="";}
		if(seconds>0) {s2=String.valueOf(seconds); if(seconds==1){s2u=" second ";}else{s2u=" seconds ";}} else {s2="";s2u="";}
		
		timeText2 = y2 + y2u + d2 + d2u + h2 + h2u + m2 + m2u + s2 + s2u;
		
		
	}

}
