package com.surevine.community.gateway.audit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
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

import org.apache.commons.io.IOUtils;
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
public class XMLAuditServiceImpl implements AuditService {

	public static XMLAuditServiceImpl _instance = null;

	private static final Logger LOG = Logger.getLogger(XMLAuditServiceImpl.class.getName());
	private final Properties config = new Properties();
	private DocumentBuilder documentBuilder;
	private final SimpleDateFormat dateFormat;
	private String auditLogFile;

	private XMLAuditServiceImpl() {
		final InputStream stream = getClass().getResourceAsStream("/audit.properties");
		try {
			config.load(stream);
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "Failed to load audit module configuration. ", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (final IOException e) {
					LOG.log(Level.SEVERE, "Failed to load audit module configuration. ", e);
				}
			}
		}

		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			throw new AuditServiceException("Unable to init XML audit service.", e);
		}
	}

	public static XMLAuditServiceImpl getInstance() {
		if (_instance == null) {
			_instance = new XMLAuditServiceImpl();
		}
		return _instance;
	}

	@Override
	public void audit(final AuditAction action) {

		final String logfileDirectory = config.getProperty("gateway.audit.xml.logfile.dir");
		final Path auditFile = Paths.get(logfileDirectory, "audit.xml");
		if (!Files.exists(auditFile)) {
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

		final Node importedEventNode = document.importNode(event, true);
		final Element events = document.getDocumentElement();
		events.appendChild(importedEventNode);

		persistEvent(document);

	}

	/**
	 * Creates audit XML file to log events to (if file doesn't exist)
	 */
	private void createAuditFile(final Path auditFile) {

		if (!Files.exists(auditFile)) {
			LOG.info("No existing XML audit file found. Creating new file.");
			final InputStream auditTemplateStream = getClass().getResourceAsStream("/audit-file-template.xml");
			try {
				Files.copy(auditTemplateStream, auditFile);
			} catch (final IOException e) {
				throw new AuditServiceException("Could not create new XML audit log file.", e);
			} finally {
				if (auditTemplateStream != null) {
					try {
						auditTemplateStream.close();
					} catch (final IOException e) {
						throw new AuditServiceException("Could not create new XML audit log file.", e);
					}
				}
			}
		} else {
			LOG.info("Found existing XML audit file.");
		}

	}

	/**
	 * Generates a new Event node to be added to XML audit log
	 *
	 * @param event
	 *            Audited action
	 * @return XML Node representing audit event
	 * @throws SAXException
	 * @throws IOException
	 */
	private Node createEventXML(final AuditAction action) throws SAXException, IOException {

		final String eventTemplate = loadEventTemplate();
		final String populatedEvent = populateEventTemplate(eventTemplate, action);
		final InputStream eventInputStream = new ByteArrayInputStream(populatedEvent.getBytes("UTF-8"));
		final Node eventNode = documentBuilder.parse(eventInputStream).getFirstChild();

		return eventNode;
	}

	/**
	 * Loads the XML template for an event from disk
	 *
	 * @return Contents of event template
	 */
	private String loadEventTemplate() {
		final InputStream eventTemplateStream = getClass().getResourceAsStream("/audit-event-template.xml");
		String eventTemplate;
		try {
			eventTemplate = IOUtils.toString(eventTemplateStream, Charset.defaultCharset());
		} catch (final IOException e) {
			throw new AuditServiceException("Unable to load audit event template.", e);
		} finally {
			if (eventTemplateStream != null) {
				try {
					eventTemplateStream.close();
				} catch (final IOException e) {
					throw new AuditServiceException("Unable to load audit event template.", e);
				}
			}
		}

		return eventTemplate;
	}

	/**
	 * Writes an audit event to disk
	 *
	 * @param document
	 * @throws TransformerFactoryConfigurationError
	 */
	private void persistEvent(final Document document) {
		final DOMSource source = new DOMSource(document);
		final StreamResult result = new StreamResult(auditLogFile);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.transform(source, result);
		} catch (final TransformerException e) {
			throw new AuditServiceException("Unable to write audit event to log file.", e);
		}
	}

	/**
	 * Populates an event template with audit event values
	 *
	 * @param template
	 *            Template to populate
	 * @param event
	 *            Audited action
	 * @return String populated event template string
	 */
	private String populateEventTemplate(String template, final AuditAction action) {

		template = template.replace("%EVENT_TIME%", dateFormat.format(new Date()));
		template = template.replace("%EVENT_SYSTEM_ENVIRONMENT%",
				config.getProperty("gateway.audit.xml.system.environment"));
		template = template.replace("%EVENT_ACTION%", action.serialize());

		return template;
	}

}
