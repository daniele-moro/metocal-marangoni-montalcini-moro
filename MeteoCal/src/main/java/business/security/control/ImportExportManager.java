package business.security.control;

import business.security.entity.Event;
import exception.DateConsistencyException;
import exception.ImportExportException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.validator.Validator;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.codehaus.jettison.json.JSONException;
import org.iso_relax.verifier.Schema;

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

    /**
     * This method does the import functionality, it checks before if all the
     * events the user wants to import are correct and compatible with the
     * system and after it adds the events
     *
     * @param inStr InputStream of the fileyou want to import
     * @throws JAXBException
     * @throws exception.ImportExportException
     * @throwsImportExportException Exception in case of not compatible events
     * (overlaps or outdoor events without accepted weather condition)
     * @throws DateConsistencyException
     * @throws JSONException
     */
    public void importUserCalendar(InputStream inStr) throws DateConsistencyException, JSONException, ImportExportException {
        JAXBContext context;
        EventsList events;
        try {
            context = JAXBContext.newInstance(EventsList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            events = (EventsList) unmarshaller.unmarshal(inStr);
        } catch (JAXBException ex) {
            throw new ImportExportException("Error: the calendar has some missing fields for some events, or for his structure");
        }

        for (Event e : events.getEvents()) {
            //Controllo se la data degli eventi da importare è compatibile con gli eventi del DB
            //inoltre controllo che se l'evento è outdoor abbia l'AcceptedWeatherCondition
            if (!eventMgr.checkDateConsistency(e)) {
                //se un evento provoca sovrapposizioni, genero un eccezione
                throw new ImportExportException("You may have some overlapping events or you have some inconsistency on the date, the event with problem is " + '"' + e.getName() + '"');
            }
            if (e.isOutdoor() && e.getAcceptedWeatherConditions() == null) {
                throw new ImportExportException("Outdoor events must have accepted weather condition, the event with problem is " + '"' + e.getName() + '"');
            }

            //Non servirebbe perchè viene gia fatto dentro il create event
            e.setOrganizer(eventMgr.getLoggedUser());
        }
        //Aggiungo tutti gli eventi al Database
        for (Event e : events.getEvents()) {
            eventMgr.createEvent(e);
        }
    }

    /**
     * * This method does the export functionality, it takes alla the events of
     * the logged user and it exports them in a .xml file
     *
     * @return ByteArrayOutputStream stream of the output
     * @throws JAXBException
     */
    public ByteArrayOutputStream exportUserCalendar() throws JAXBException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXBContext context = JAXBContext.newInstance(EventsList.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        //Lista di eventi da portare in out sul file xml
        EventsList events = new EventsList();

        events.setEvents(uil.loadCreatedEvents());

        events.addEvents(uil.loadAcceptedEvents());
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

    public List<Event> getEvents() {
        return this.events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void addEvents(List<Event> events) {
        this.events.addAll(events);
    }
}
