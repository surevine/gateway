package com.surevine.community.gateway.hooks;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		final String transferExtension = GatewayProperties.get(GatewayProperties.TRANSFER_EXTENSION);
		for (final URI uri : destinations) {
			if ("sftp".equals(uri.getScheme())) {
				final String intermediateFileName = source.getFileName().toString()
						+transferExtension;
				final String intermediatePath = Paths.get(uri.getPath().toString(), intermediateFileName).toString();
				final String destinationPath = intermediatePath.substring(
						0, (intermediatePath.length() - transferExtension.length()));
				
				LOG.info(String.format(
						"Calling sftp for %s to %s@%s:%s",
						source, uri.getUserInfo(), uri.getHost(), intermediatePath));
	
				final JSch jsch = new JSch();
				try {
					jsch.addIdentity(getIdentity(uri));
					final Session session = uri.getUserInfo() == null ?
							jsch.getSession(uri.getHost()) : jsch.getSession(
							uri.getUserInfo(), uri.getHost());
					session.setConfig("StrictHostKeyChecking", "no");
					session.connect();
					
					// File transfer to temporary location
					final ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
					sftpChannel.setInputStream(System.in);
					sftpChannel.setOutputStream(System.out);
					sftpChannel.connect();
					sftpChannel.put(source.toString(), intermediatePath);
					
					LOG.info(String.format("Calling move from %s to %s.",
							intermediatePath, destinationPath));

					sftpChannel.chmod(Integer.parseInt("644", 8), intermediatePath);
					sftpChannel.rename(intermediatePath, destinationPath);
					sftpChannel.disconnect();
					
					session.disconnect();
				} catch (final Exception e) {
					LOG.severe(e.getMessage());
					LOG.log(Level.INFO, e.getMessage(), e);
				}
			}
		}
	}
	
	private String getIdentity(final URI uri) {
		final String identity = get(uri.getHost(), "key");
		
		// Defaults to ~/.ssh/id_rsa if no key specified for the given host(uri).
		return identity != null ?
				identity : Paths.get(System.getProperty("user.home"),
						".ssh", "id_rsa").toString();
	}
	
	private String get(final String host, final String key) {
		final String bundleKey = String.format("gateway.sftp.%s.%s", host, key);
		
		return config.containsKey(bundleKey) ? config.getString(bundleKey) : null;
	}
}
