package com.surevine.community.gateway.hooks;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Passes imported file details to scp.
 * 
 * @author rich.midwinter@gmail.com
 */
public class SftpExportTransferHook implements GatewayExportTransferHook {

	private static final Logger LOG = Logger.getLogger(SftpExportTransferHook.class.getName());
	
	private ResourceBundle config;
	
	public SftpExportTransferHook() {
		config = ResourceBundle.getBundle("sftp-plugin");
	}

	public void call(final Path source, final Map<String, String> properties,
			final URI... destinations) {
		for (final URI uri : destinations) {
			if ("sftp".equals(uri.getScheme())) {
				LOG.info(String.format(
						"Calling scp for %s on host %s and path %s with source %s",
						uri.getUserInfo(), uri.getHost(), uri.getPath(), source));
	
				final JSch jsch = new JSch();
				try {
					jsch.addIdentity(get(uri.getHost(), "key"));
					final Session session = jsch.getSession(
							uri.getUserInfo(),
							uri.getHost());
					session.setConfig("StrictHostKeyChecking", "no");
					session.connect();
					
					final ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
					channel.setInputStream(System.in);
					channel.setOutputStream(System.out);
					channel.connect();
					channel.put(source.toString(), uri.getPath());
					channel.exit();
				} catch (final Exception e) {
					LOG.severe(e.getMessage());
					LOG.log(Level.INFO, e.getMessage(), e);
				}
			}
		}
	}
	
	public String get(final String host, final String key) {
		return config.getString(String.format("gateway.sftp.%s.%s", host, key));
	}
}
