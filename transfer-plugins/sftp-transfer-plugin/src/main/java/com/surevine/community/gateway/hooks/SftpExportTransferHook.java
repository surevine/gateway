package com.surevine.community.gateway.hooks;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.surevine.community.gateway.GatewayProperties;

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
				final String intermediate = Paths.get(uri.getPath(),
						source.getFileName().toString() +GatewayProperties.get(GatewayProperties.TRANSFER_EXTENSION)).toString();
				
				LOG.info(String.format(
						"Calling sftp for %s to %s@%s:%s",
						source, uri.getUserInfo(), uri.getHost(), intermediate));
	
				final JSch jsch = new JSch();
				try {
					jsch.addIdentity(get(uri.getHost(), "key"));
					final Session session = jsch.getSession(
							uri.getUserInfo(),
							uri.getHost());
					session.setConfig("StrictHostKeyChecking", "no");
					session.connect();
					
					// File transfer to temporary location
					final ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
					sftpChannel.setInputStream(System.in);
					sftpChannel.setOutputStream(System.out);
					sftpChannel.connect();
					sftpChannel.put(source.toString(), intermediate);
					sftpChannel.disconnect();
					
					final String move = String.format("mv %s %s",
							Paths.get(uri.getPath(), source.toString()
									+GatewayProperties.get(GatewayProperties.TRANSFER_EXTENSION)).toString(),
							Paths.get(uri.getPath(), source.toString()).toString());
					
					LOG.info("Calling remote move command: " +move);
					
					// Move from temporary location to destination
					final ChannelExec execChannel = (ChannelExec) session.openChannel("exec");
					execChannel.setCommand(move);
					execChannel.disconnect();
					
					session.disconnect();
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
