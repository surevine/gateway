package com.surevine.community.gateway.audit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import com.surevine.community.gateway.audit.action.AuditAction;
import com.surevine.community.gateway.audit.action.AuditActionFactory;
import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.audit.action.ImportAuditAction;
import com.surevine.community.gateway.audit.action.RuleFailAuditAction;
import com.surevine.community.gateway.audit.action.SanitisationFailAuditAction;
import com.surevine.community.gateway.audit.action.logfile.LogAuditActionFactory;
import com.surevine.community.gateway.audit.action.xml.XMLAuditActionFactory;
import com.surevine.community.gateway.model.Destination;

/**
 * @author jonnyheavey
 */
public abstract class Audit {

	private static AuditService auditServiceImpl;
	private static AuditActionFactory auditActionFactoryImpl;
	private static Properties config = new Properties();

	/**
	 * Initialise the AuditService based on config property
	 * @return
	 */
	private static AuditService getAuditService() {
		if(auditServiceImpl == null) {
			switch(getAuditModeSetting()) {
			case XML:
				auditServiceImpl = XMLAuditServiceImpl.getInstance();
				break;
			case LOG:
				auditServiceImpl = new LogAuditServiceImpl();
				break;
			default:
				throw new AuditServiceException("Could not initialise application auditing. Auditing mode not correctly configured.");
			}
		}
		return auditServiceImpl;
	}

	/**
	 * Initialise the AuditActionFactory based on config property
	 * @return
	 */
	private static AuditActionFactory getAuditActionFactory() {
		if(auditActionFactoryImpl == null) {
			switch(getAuditModeSetting()) {
			case XML:
				auditActionFactoryImpl = new XMLAuditActionFactory();
				break;
			case LOG:
				auditActionFactoryImpl = new LogAuditActionFactory();
				break;
			default:
				throw new AuditServiceException("Could not initialise application auditing. Auditing mode not correctly configured.");
			}
		}
		return auditActionFactoryImpl;
	}

	/**
	 * Retrieve audit mode setting from property file.
	 * Used to conditionally initialise services.
	 *
	 * @return configured audit mode
	 */
	private static AuditMode getAuditModeSetting() {
		try {
			config.load(Audit.class.getClassLoader().getResourceAsStream("/audit.properties"));
			return AuditMode.getMode(config.getProperty("gateway.audit.mode"));
		} catch (IOException e) {
			throw new AuditServiceException("Failed to load audit service property.", e);
		}
	}

	/**
	 * Get action representing item export
	 * @param filename exported file
	 * @param destination partner/system the file was exported to
	 * @return
	 */
	public static ExportAuditAction getExportAuditAction(String filename, Destination destination){
		return getAuditActionFactory().getExportAuditAction(filename, destination);
	}

	/**
	 * Get action representing item import
	 * @param filename file imported
	 * @param source partner/system the file was sent from
	 * @return
	 */
	public static ImportAuditAction getImportAuditAction(String filename, String source) {
		return getAuditActionFactory().getImportAuditAction(filename, source);
	}

	/**
	 * Get action representing export rule failure
	 * @param source file to be exported
	 * @param destination intended export destination
	 * @return
	 */
	public static RuleFailAuditAction getRuleFailAuditAction(Path source, Destination destination) {
		return getAuditActionFactory().getRuleFailAuditAction(source, destination);
	}

	/**
	 * Get action representing sanitisation check failure
	 * @param source file to be exported
	 * @param destination intended export destination
	 * @return
	 */
	public static SanitisationFailAuditAction getSanitisationFailAuditAction(Path source, Destination destination) {
		return getAuditActionFactory().getSanitisationFailAuditAction(source, destination);
	}

	/**
	 * Record action via configured audit service
	 * @param action action to be recorded
	 */
	public static void audit(AuditAction action) {
		getAuditService().audit(action);
	}

	/**
	 * Explicitly set an audit service (ignoring configuration).
	 * Primarily used for unit test mocking.
	 *
	 * @param auditService
	 */
	public static void setAuditService(AuditService auditService) {
		auditServiceImpl = auditService;
	}

	/**
	 * Explicitly set an audit action factory (ignoring configuration).
	 * Primarily used for unit test mocking.
	 *
	 * @param auditActionFactory
	 */
	public static void setAuditActionFactory(AuditActionFactory auditActionFactory) {
		auditActionFactoryImpl = auditActionFactory;
	}

}
