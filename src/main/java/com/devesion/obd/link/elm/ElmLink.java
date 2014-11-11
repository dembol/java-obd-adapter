package com.devesion.obd.link.elm;

import com.devesion.obd.command.CommandResult;
import com.devesion.obd.command.ObdCommand;
import com.devesion.obd.link.CommandMarshaller;
import com.devesion.obd.link.CommandUnmarshaller;
import com.devesion.obd.link.ObdLink;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents ELM link between two nodes (ie. bluetooth device and PC).
 */
@Slf4j
public class ElmLink implements ObdLink {

	private final ElmTransport transport;

	@Getter(AccessLevel.PACKAGE)
	private CommandMarshaller commandMarshaller;

	@Getter(AccessLevel.PACKAGE)
	private CommandUnmarshaller commandUnmarshaller;

	public ElmLink(InputStream inputStream, OutputStream outputStream) {
		this(new ElmTransport(inputStream, outputStream));
	}

	public ElmLink(ElmTransport transport) {
		this(transport, new CommandMarshallerBridge(), new CommandUnmarshallerBridge());
	}

	public ElmLink(ElmTransport transport, CommandMarshaller commandMarshaller, CommandUnmarshaller commandUnmarshaller) {
		this.transport = transport;
		this.commandMarshaller = commandMarshaller;
		this.commandUnmarshaller = commandUnmarshaller;
	}

	@Override
	public CommandResult sendCommand(ObdCommand command) {
		String commandData = commandMarshaller.marshal(command);
		String commandResultData = transport.sendDataAndReadResponse(commandData);
		return commandUnmarshaller.unmarshal(command, commandResultData);
	}
}
