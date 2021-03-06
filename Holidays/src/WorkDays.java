import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class WorkDays {
	
	private List<LocalDate> workingDays = new ArrayList<>();
	private List<LocalDate> regularVacations = new ArrayList<>();
	private List<LocalDate> extraVacations = new ArrayList<>();
	private List<LocalDate> extraWorkingDays = new ArrayList<>();
	private Properties config;
	
	public static void main(String[] args) {
		WorkDays workDays = new WorkDays("config.property");
		workDays.process();
		for (LocalDate workday : workDays.getWorkingDays()) {
		}
	}
	

	public  List<LocalDate> getWorkingDays() {
		return workingDays;
	}

	public WorkDays(String configFile) {
		config = readProperties("config.property");
		
		 Set<Object> keys = config.keySet();
		 for (Iterator<Object> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			//processing extra workdays
			if(key.contains("extra-workday") && !key.contains("extra-workday-format")) {
				DateTimeFormatter extraWorkingDaysFormatter = DateTimeFormatter.ofPattern(config.getProperty("extra-workday-format"));
				extraWorkingDays.add(LocalDate.parse((String)config.get(key), extraWorkingDaysFormatter));
			}
			//processing regular vacations
			if(key.contains("vacation") && !key.contains("vacation-format") && !key.contains("extra")) {
				DateTimeFormatter regularVacationsFormatter = DateTimeFormatter.ofPattern("yyyy-" + config.getProperty("vacation-format"));
				LocalDate tmpLocalDate = LocalDate.parse(LocalDateTime.now().getYear() + "-" + (String)config.get(key), regularVacationsFormatter);
				regularVacations.add(tmpLocalDate);
			}
			//processing extra vacations
			if(key.contains("extra-vacation") && !key.contains("extra-vacation-format")) {
				DateTimeFormatter extraVacationsFormatter = DateTimeFormatter.ofPattern((config.getProperty("extra-vacation-format")));
				LocalDate tmpLocalDate = LocalDate.parse((String)config.get(key), extraVacationsFormatter);
				extraVacations.add(tmpLocalDate);
			}
			
		}
		 
	}
	
	public void process() {
		
		LocalDate dateOfNow = LocalDate.now();
		LocalDate endOfDateCalculation = LocalDate.of(2016, 12, 31);
		LocalDate calculationDate = dateOfNow;
		
		while(!calculationDate.equals(endOfDateCalculation)) {
			if(isWorkingDay(calculationDate)) {
				workingDays.add(calculationDate);
			}
			calculationDate = calculationDate.plusDays(1); //moving the calendar pointer towards a day
		}
	}
	
	private boolean isWorkingDay(LocalDate calculationDate) {
		if (isWeekDay(calculationDate)) {
			return true;
		}
		else if (isExtraWorkingDay(calculationDate)) {
			return true;
		}
		else if (isRegularVacation(calculationDate)) {
			return false;
		}
		else if (isExtraVacation(calculationDate)) {
			return false;
		}				
		return false;
	}

	private boolean isExtraVacation(LocalDate calculationDate) {
		return extraVacations.contains(calculationDate);
	}


	private boolean isRegularVacation(LocalDate calculationDate) {
		return regularVacations.contains(calculationDate);
	}


	private boolean isExtraWorkingDay(LocalDate calculationDate) {
		return extraWorkingDays.contains(calculationDate);
	}

	private boolean isWeekDay(LocalDate calculationDate) {
		return ((calculationDate.getDayOfWeek() != DayOfWeek.SATURDAY) && 
				(calculationDate.getDayOfWeek() != DayOfWeek.SUNDAY));
	}

	public Properties readProperties(String fileName) {
		Properties prop = new Properties();		
		try {
			InputStream in = getClass().getResourceAsStream(fileName);
			prop.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return prop;
	}

}
