package business.security.boundary;

import business.security.entity.Event;
import exception.DateConsistencyException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jettison.json.JSONException;

/**
 *
 * @author Daniele Moro
 */
@Stateless
public class ImportExportManager {
    
    @EJB
    EventManager eventMgr;
    
    @EJB
    UserInformationLoader uil;
    
    public void importUserCalendar(InputStream inStr) throws JAXBException, DateConsistencyException, JSONException {
        JAXBContext context = JAXBContext.newInstance(EventsList.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        
        EventsList events = (EventsList) unmarshaller.unmarshal(inStr);
        
        for(Event e : events.getEvents()){
            //Controllo se la data degli eventi da importare è compatibile con gli eventi del DB
            if(!eventMgr.checkDateConsistency(e)){
                //se un evento provoca sovrapposizioni, genero un eccezione
                throw new DateConsistencyException("Error in date consistency on import");
            }
            //Non servirebbe perchè viene gia fatto dentro il create event
            e.setOrganizer(eventMgr.getLoggedUser());
        }
        //Aggiungo tutti gli eventi al Database
        for(Event e : events.getEvents()){
            eventMgr.createEvent(e);
        }
    }
    
    public ByteArrayOutputStream exportUserCalendar() throws JAXBException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXBContext context = JAXBContext.newInstance(EventsList.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        //Lista di eventi da portare in out sul file xml
        EventsList events = new EventsList();
        
        events.setEvents(uil.loadCreatedEvents());
        // Write to OutputStream
        m.marshal(events, out);
        System.out.println(Arrays.toString(out.toByteArray()));
        return out;
    }
}

//Classe Temporanea solo per importare ed esportare su XML la lista di eventi
@XmlRootElement(name = "calendar")
        class EventsList {
    List<Event> events = new ArrayList<Event>();
    
    public List<Event> getEvents(){
        return this.events;
    }
    public void setEvents(List<Event> events){
        this.events=events;
    }
}
