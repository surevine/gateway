package com.surevine.community.gateway.audit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.surevine.community.gateway.audit.action.AuditAction;

/**
 * Audit Service implementation to log audit events to XML file.
 *
 * @author jonnyheavey
 *
 */
public class GatewayXMLAuditServiceImpl implements AuditService {

	public static GatewayXMLAuditServiceImpl _instance = null;

	private static final Logger LOG = Logger.getLogger(GatewayXMLAuditServiceImpl.class.getName());
	private Properties config = new Properties();
	private DocumentBuilder documentBuilder;
	private SimpleDateFormat dateFormat;
	private String auditLogFile;

	private GatewayXMLAuditServiceImpl()  {
		try {
			getConfig().load(getClass().getResourceAsStream("/audit.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new AuditServiceException("Unable to init XML audit service.", e);
		}
	}

	public static GatewayXMLAuditServiceImpl getInstance() {
		if(_instance == null) {
			_instance = new GatewayXMLAuditServiceImpl();
		}
		return _instance;
	}

	@Override
	public void audit(AuditAction action) {

		String logfileDirectory = config.getProperty("gateway.audit.logfile.dir");
		Path auditFile = Paths.get(logfileDirectory, "audit.xml");
		if(!Files.exists(auditFile)) {
			createAuditFile(auditFile);
		}
		this.auditLogFile = auditFile.toString();

        Document document;
        Node event;
		try {
			document = documentBuilder.parse(auditLogFile);
			event = createEventXML(action);
		} catch (SAXException | IOException e) {
			throw new AuditServiceException("Unable to load XML audit log file.", e);
		}

		Node importedEventNode = document.importNode(event, true);
		Element events = document.getDocumentElement();
		events.appendChild(importedEventNode);

        persistEvent(document);

	}

	/**
	 * Creates audit XML file to log events to (if file doesn't exist)
	 */
	private void createAuditFile(Path auditFile) {

		if(!Files.exists(auditFile)) {
			LOG.info("No existing XML audit file found. Creating new file.");
			InputStream auditTemplateStream = getClass().getResourceAsStream("/audit-file-template.xml");
			try {
				Files.copy(auditTemplateStream, auditFile);
			} catch (IOException e) {
				throw new AuditServiceException("Could not create new XML audit log file.", e);
			}
		} else {
			LOG.info("Found existing XML audit file.");
		}

	}

	/**
	 * Generates a new Event node to be added to XML audit log
	 *
	 * @param event Audited action
	 * @return XML Node representing audit event
	 * @throws SAXException
	 * @throws IOException
	 */
	private Node createEventXML(AuditAction action) throws SAXException, IOException {

		String eventTemplate = loadEventTemplate();
		String populatedEvent = populateEventTemplate(eventTemplate, action);
		InputStream eventInputStream = new ByteArrayInputStream(populatedEvent.getBytes("UTF-8"));
		Node eventNode = documentBuilder.parse(eventInputStream).getFirstChild();

		return eventNode;
	}

	private Properties getConfig() {
		return config;
	}

	/**
	 * Loads the XML template for an event from disk
	 *
	 * @return Contents of event template
	 */
	private String loadEventTemplate() {

		StringBuffer parsedEventTemplate = new StringBuffer();

		try {
			Path eventTemplate = Paths.get(getClass().getResource("/audit-event-template.xml").toURI());
			List<String> lines = Files.readAllLines(eventTemplate, Charset.defaultCharset());
			for (String line : lines) {
				parsedEventTemplate.append(line + System.getProperty("line.separator"));
			}
		} catch (IOException | URISyntaxException e) {
			throw new AuditServiceException("Unable to load audit event template.", e);
		}

		return parsedEventTemplate.toString();
	}

	/**
	 * Writes an audit event to disk
	 *
	 * @param document
	 * @throws TransformerFactoryConfigurationError
	 */
	private void persistEvent(Document document) {
		DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(auditLogFile);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new AuditServiceException("Unable to write audit event to log file.", e);
		}
	}

	/**
	 * Populates an event template with audit event values
	 *
	 * @param template Template to populate
	 * @param event Audited action
	 * @return String populated event template string
	 */
	private String populateEventTemplate(String template, AuditAction action) {

		template = template.replace("%EVENT_TIME%", dateFormat.format(new Date()));
		template = template.replace("%EVENT_SYSTEM_ENVIRONMENT%", config.getProperty("gateway.system.environment"));
		template = template.replace("%EVENT_ACTION%", action.serialize());

		return template;
	}

}
