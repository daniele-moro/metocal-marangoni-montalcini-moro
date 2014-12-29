package gui.security;

import business.security.boundary.ImportExportManager;
import exception.DateConsistencyException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Daniele Moro
 */
@Named
public class ImportExportCalendarBean {
    
    @EJB
    ImportExportManager impExp;
    
    public void handleFileUpload(FileUploadEvent event) throws JAXBException{
        FacesMessage message;
        UploadedFile file=event.getFile();
        
        if(file != null) {
            try {
                impExp.importUserCalendar(file.getInputstream());
                message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            } catch (IOException | DateConsistencyException ex) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", file.getFileName() + " isn't uploaded. \n"+ex.getMessage());
            }
        }else{
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "File not uploaded");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
        
    }
    
    public StreamedContent handleExportCalendar(){
        StreamedContent file = null;
        FacesMessage message;
        ByteArrayOutputStream out;
        
        try {
            out = impExp.exportUserCalendar();
            file = new DefaultStreamedContent(new ByteArrayInputStream(out.toByteArray()),"text/xml", "calendar.xml");
            message = new FacesMessage("Information", "File Created correctly");
        } catch (JAXBException ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", "Error on downloading calendar file\n" + ex.getMessage());
            // Logger.getLogger(ExportCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
        return file;
    }
}
