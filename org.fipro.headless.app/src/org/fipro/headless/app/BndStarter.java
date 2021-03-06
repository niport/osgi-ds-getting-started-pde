package org.fipro.headless.app;

import java.util.Arrays;
import java.util.Map;

import org.fipro.inverter.StringInverter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class BndStarter {

	/**
	 * Launcher arguments provided by the bnd launcher.
	 */
	String[] launcherArgs;

	@Reference(target = "(launcher.arguments=*)")
	void setLauncherArguments(Object object, Map<String, Object> map) {
		this.launcherArgs = (String[]) map.get("launcher.arguments");
	}

	@Reference
	StringInverter inverter;

	@Activate
	void activate() {
		boolean isConsoleConfigured = System.getProperty("osgi.console") != null;
		
		boolean isInteractive = Arrays.stream(launcherArgs)
				.anyMatch(arg -> "-console".equals(arg));

		// clear launcher arguments from possible framework parameter
		String[] args = Arrays.stream(launcherArgs)
				.filter(arg -> !"-console".equals(arg) && !"-consoleLog".equals(arg))
				.toArray(String[]::new);

		for (String arg : args) {
			System.out.println(inverter.invert(arg));
		}

		if (!isConsoleConfigured
				|| (isConsoleConfigured && !isInteractive)) {
			// shutdown the application if no console was opened
			// only needed if osgi.noShutdown=true is configured
			System.exit(0);
		}
	}
}