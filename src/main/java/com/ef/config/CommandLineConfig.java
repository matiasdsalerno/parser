package com.ef.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CommandLineConfig {

    public static final String ACCESSLOG_ARG_KEY = "accesslog";
    public static final String DURATION_ARG_KEY = "duration";
    public static final String THRESHOLD_ARG_KEY = "threshold";
    public static final String START_DATE_ARG_KEY = "startDate";

    public Options buildCommandLineOptions() {
        Options options = new Options();
        Option accesslog = Option.builder()
                .longOpt(ACCESSLOG_ARG_KEY)
                .valueSeparator('=')
                .hasArg(true)
                .required()
                .desc("accesslog file path").build();
        options.addOption(accesslog);

        Option duration = Option.builder()
                .longOpt(DURATION_ARG_KEY)
                .valueSeparator('=')
                .hasArg(true)
                .required()
                .desc("duration type. It can be hourly or daily").build();
        options.addOption(duration);

        Option threshold = Option.builder()
                .longOpt(THRESHOLD_ARG_KEY)
                .valueSeparator('=')
                .hasArg(true)
                .required()
                .desc("access threshold to server by an IP address").build();
        options.addOption(threshold);

        Option startDate = Option.builder()
                .longOpt(START_DATE_ARG_KEY)
                .valueSeparator('=')
                .hasArg(true)
                .required()
                .desc("start date for ip scan").build();
        options.addOption(startDate);
        return options;
    }
}
