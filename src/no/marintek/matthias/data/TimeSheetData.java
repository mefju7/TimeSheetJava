package no.marintek.matthias.data;

import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "TimeSheet")
public class TimeSheetData {
	

	@XmlTransient
	public boolean showBilling;
	@XmlElement(name = "entry")
	public List<TimeSheetEntry> entries;
	
	@XmlTransient
	public TreeMap<String,TreeMap<String, TimeSheetEntry>> projTask;

	@XmlTransient
	PropertyChangeSupport changed = new PropertyChangeSupport(this);
	private TimeSheetEntry lastEntry;
	public Date nextTurnOver=new Date();
	
	public TimeSheetData() {
		entries = new ArrayList<TimeSheetEntry>();
		// projTask=new HashMap<String, Map<String,TimeSheetEntry>>();
		projTask=new TreeMap<String, TreeMap<String,TimeSheetEntry>>();
	}
	
	public static TimeSheetData loadData(String fn) {
		TimeSheetData timedata=null;
		try {
			JAXBContext jaxbContext;
			jaxbContext = JAXBContext.newInstance(TimeSheetData.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
//			um.setProperty(Marshaller.JAXB_FRAGMENT, true);
//			um.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
//			timedata = (TimeSheetData) um.unmarshal(new FileReader(fn));
			timedata = (TimeSheetData) um.unmarshal(new FileInputStream(fn));

		} catch (JAXBException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (timedata == null)
			timedata = new TimeSheetData();
		return timedata;
	}
	
	public void saveData(String dfn) {
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(TimeSheetData.class);
			Marshaller mar = jaxbContext.createMarshaller();
			mar.marshal(this, new FileOutputStream(dfn));
		} catch (JAXBException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TimeSheetData reSort() {
		// TODO Auto-generated method stub
		entries.sort(new TimeSheetEntryComparer());
		return this;
	}

	public TimeSheetData refresh() {
		// TODO Auto-generated method stub
		projTask.clear();
		TimeSheetEntry last=null;
		while(entries.size()>0)
		{
			Calendar cal=Calendar.getInstance();
			cal.setTime(new Date());
			int curYear = cal.get(Calendar.YEAR);
			TimeSheetEntry te = entries.get(0);
			cal.setTime(te.getStartTime());
			if(cal.get(Calendar.YEAR)>curYear-5)
				break;
			entries.remove(0);
			
		}
		for(TimeSheetEntry te : entries)
		{
			// first of all, get the previous entry
			 TreeMap<String, TimeSheetEntry> pm = projTask.get(te.project);
			if(pm==null)
			{
				pm=new TreeMap<String, TimeSheetEntry>();
				projTask.put(te.project, pm);
			}
			te.previous=pm.get(te.task);
			pm.put(te.task, te);
			// now calculate for the last one
			if(last !=null)
			{
				last.endTime=te.getStartTime();
				last.calculate();
			}
			last=te;
			
		}
		lastEntry=last;
		changed.firePropertyChange("refreshed",0,0);
		return this;
	}

	public void updateLast() {
		// TODO Auto-generated method stub
		if(lastEntry!=null)
		{
			
			lastEntry.endTime= new Date();
			lastEntry.calculate();
			nextTurnOver = lastEntry.getNextTurnOver();
			
		}
	}
}