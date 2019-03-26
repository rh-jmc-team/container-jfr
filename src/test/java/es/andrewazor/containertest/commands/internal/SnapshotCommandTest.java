package es.andrewazor.containertest.commands.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjdk.jmc.common.unit.IConstrainedMap;
import org.openjdk.jmc.rjmx.services.jfr.IFlightRecorderService;
import org.openjdk.jmc.rjmx.services.jfr.IRecordingDescriptor;

import es.andrewazor.containertest.JMCConnection;
import es.andrewazor.containertest.RecordingExporter;
import es.andrewazor.containertest.StdoutTest;

@ExtendWith(MockitoExtension.class)
class SnapshotCommandTest extends StdoutTest {

    private SnapshotCommand command;
    @Mock private JMCConnection connection;
    @Mock private IFlightRecorderService service;
    @Mock private RecordingExporter exporter;

    @BeforeEach
    void setup() {
        command = new SnapshotCommand(exporter);
        command.connectionChanged(connection);
    }

    @Test
    void shouldBeNamedSnapshot() {
        MatcherAssert.assertThat(command.getName(), Matchers.equalTo("snapshot"));
    }

    @Test
    void shouldExpectNoArgs() {
        assertTrue(command.validate(new String[0]));
        assertFalse(command.validate(new String[1]));
    }

    @Test
    @Disabled("RecordingOptionsBuilder needs refactoring so mock IFlightRecorderServices can be used")
    void shouldRenameAndExportSnapshot() throws Exception {
        IRecordingDescriptor snapshot = mock(IRecordingDescriptor.class);
        when(connection.getService()).thenReturn(service);
        when(service.getSnapshotRecording()).thenReturn(snapshot);

        when(snapshot.getName()).thenReturn("Snapshot");
        when(snapshot.getId()).thenReturn(1L);

        verifyZeroInteractions(connection);
        verifyZeroInteractions(service);
        verifyZeroInteractions(exporter);
        MatcherAssert.assertThat(stdout.toString(), Matchers.emptyString());

        command.execute(new String[0]);

        MatcherAssert.assertThat(stdout.toString(), Matchers.equalTo("Latest snapshot: \"snapshot-1\"\n"));
        verify(service).getSnapshotRecording();
        verify(service).updateRecordingOptions(snapshot, Mockito.any(IConstrainedMap.class));

        ArgumentCaptor<IRecordingDescriptor> captor = ArgumentCaptor.forClass(IRecordingDescriptor.class);
        verify(exporter).addRecording(captor.capture());
        IRecordingDescriptor renamed = captor.getValue();
        MatcherAssert.assertThat(renamed.getName(), Matchers.equalTo("snapshot-1"));
    }

}