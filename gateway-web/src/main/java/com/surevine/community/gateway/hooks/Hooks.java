package com.surevine.community.gateway.hooks;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

import com.google.common.base.Joiner;
import com.surevine.community.gateway.history.History;
import com.surevine.community.gateway.model.TransferItem;

public class Hooks {

	private static final Logger LOG = Logger.getLogger(Hooks.class.getName());

	/**
	 * Call all hooks which review files on receipt, bound for export.
	 *
	 * @param source
	 *            Path to file in quarantine.
	 * @param properties
	 *            Metadata associated with the file.
	 * @throws GatewayTransferException
	 *             If submission should be rejected.
	 */
	public static void callPreExport(final Set<TransferItem> transferQueue) {
		final ServiceLoader<GatewayPreExportHook> hooks = ServiceLoader.load(GatewayPreExportHook.class);

		for (final GatewayPreExportHook hook : hooks) {
			LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
			History.getInstance().add(String.format("Running %s.", hook.getClass().getSimpleName()));

			hook.call(transferQueue);

			LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
		}
	}

	/**
	 * Call all hooks which transfer files.
	 *
	 * @param source
	 *            The file to transfer.
	 * @param properties
	 *            The whitelisted properties to transfer with the file.
	 * @param destinations
	 *            An array of URIs to transfer to.
	 */
	public static void callExportTransfer(final Set<TransferItem> transferQueue) {
		final ServiceLoader<GatewayExportTransferHook> hooks = ServiceLoader.load(GatewayExportTransferHook.class);

		for (final GatewayExportTransferHook hook : hooks) {
			LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
			History.getInstance().add(String.format("Running %s.", hook.getClass().getSimpleName()));
			hook.call(transferQueue);
			LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
		}
	}

	/**
	 * Call all hooks which review files on receipt, bound for local import.
	 *
	 * @param source
	 *            Path to file in quarantine.
	 * @param properties
	 *            Metadata associated with the file.
	 * @throws GatewayTransferException
	 *             If submission should be rejected.
	 */
	public static void callPreImport(final Path source, final Map<String, String> properties) {
		final ServiceLoader<GatewayPreImportHook> hooks = ServiceLoader.load(GatewayPreImportHook.class);

		for (final GatewayPreImportHook hook : hooks) {
			LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
			History.getInstance().add(
					String.format("Running %s against %s.", hook.getClass().getSimpleName(),
							source.getName(source.getNameCount() - 1)));

			hook.call(source, properties);

			LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
		}
	}

	/**
	 * Call all hooks which transfer imported files to local systems.
	 *
	 * @param source
	 *            Path to file.
	 * @param properties
	 *            Metadata associated with the file.
	 */
	public static void callImportTransfer(final File[] received, final Map<String, String> properties) {
		final ServiceLoader<GatewayImportTransferHook> hooks = ServiceLoader.load(GatewayImportTransferHook.class);

		for (final GatewayImportTransferHook hook : hooks) {
			try {
				LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
				History.getInstance().add(
						String.format("Running %s against %s.", hook.getClass().getSimpleName(),
								Joiner.on(",").join(received)));
				if (hook.supports(properties)) {
					hook.call(received, properties);
				}

				LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
			} catch (final Exception e) {
				LOG.log(Level.WARNING, "Exception " + e + " during processing of " + hook.getClass().getName(), e);
			}
		}
	}

	/**
	 * Call all hooks which initialise services.
	 */
	public static void callInit(final ServletContextEvent event) {
		final ServiceLoader<GatewayContextHook> hooks = ServiceLoader.load(GatewayContextHook.class);

		for (final GatewayContextHook hook : hooks) {
			LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
			History.getInstance().add(String.format("Running %s.", hook.getClass().getSimpleName()));

			hook.init(event);

			LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
		}
	}

	/**
	 * Call all hooks which destroy services.
	 */
	public static void callDestroy(final ServletContextEvent event) {
		final ServiceLoader<GatewayContextHook> hooks = ServiceLoader.load(GatewayContextHook.class);

		for (final GatewayContextHook hook : hooks) {
			LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
			History.getInstance().add(String.format("Running %s.", hook.getClass().getSimpleName()));

			hook.destroy(event);

			LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
		}
	}
}
