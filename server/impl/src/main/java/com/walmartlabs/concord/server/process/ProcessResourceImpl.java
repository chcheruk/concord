package com.walmartlabs.concord.server.process;

import com.walmartlabs.concord.common.IOUtils;
import com.walmartlabs.concord.server.api.history.ProcessHistoryEntry;
import com.walmartlabs.concord.server.api.process.*;
import com.walmartlabs.concord.server.api.user.UserEntry;
import com.walmartlabs.concord.server.history.ProcessHistoryDao;
import com.walmartlabs.concord.server.process.PayloadParser.EntryPoint;
import com.walmartlabs.concord.server.process.pipelines.*;
import com.walmartlabs.concord.server.process.pipelines.processors.Chain;
import com.walmartlabs.concord.server.project.ProjectDao;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.siesta.Resource;
import org.sonatype.siesta.Validate;
import org.sonatype.siesta.ValidationErrorsException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Named
public class ProcessResourceImpl implements ProcessResource, Resource {

    private static final Logger log = LoggerFactory.getLogger(ProcessResourceImpl.class);

    private final ProjectDao projectDao;
    private final ProcessHistoryDao historyDao;
    private final Chain projectPipeline;
    private final Chain projectArchivePipeline;
    private final Chain archivePipeline;
    private final Chain requestPipeline;
    private final Chain resumePipeline;
    private final ProcessExecutorImpl processExecutor;
    private final ProcessAttachmentManager attachmentManager;
    private final PayloadManager payloadManager;

    @Inject
    public ProcessResourceImpl(ProjectDao projectDao,
                               ProcessHistoryDao historyDao,
                               ProjectPipeline projectPipeline,
                               ProjectArchivePipeline projectArchivePipeline,
                               SelfContainedArchivePipeline archivePipeline,
                               RequestDataOnlyPipeline requestPipeline,
                               ResumePipeline resumePipeline,
                               ProcessExecutorImpl processExecutor,
                               ProcessAttachmentManager attachmentManager,
                               PayloadManager payloadManager) {

        this.projectDao = projectDao;
        this.historyDao = historyDao;
        this.projectPipeline = projectPipeline;
        this.projectArchivePipeline = projectArchivePipeline;
        this.archivePipeline = archivePipeline;
        this.requestPipeline = requestPipeline;
        this.resumePipeline = resumePipeline;
        this.processExecutor = processExecutor;
        this.attachmentManager = attachmentManager;
        this.payloadManager = payloadManager;
    }

    @Override
    public StartProcessResponse start(InputStream in) {
        String instanceId = UUID.randomUUID().toString();

        Payload payload;
        try {
            payload = payloadManager.createPayload(instanceId, getInitiator(), in);
        } catch (IOException e) {
            throw new WebApplicationException("Error creating a payload", e);
        }

        archivePipeline.process(payload);
        return new StartProcessResponse(instanceId);
    }

    @Override
    public StartProcessResponse start(String entryPoint, Map<String, Object> req) {
        String instanceId = UUID.randomUUID().toString();

        EntryPoint ep = PayloadParser.parseEntryPoint(entryPoint);
        assertProject(ep.getProjectName());

        Payload payload;
        try {
            payload = payloadManager.createPayload(instanceId, getInitiator(), ep, req);
        } catch (IOException e) {
            throw new WebApplicationException("Error creating a payload", e);
        }

        requestPipeline.process(payload);
        return new StartProcessResponse(instanceId);
    }

    @Override
    public StartProcessResponse start(String entryPoint, MultipartInput input) {
        String instanceId = UUID.randomUUID().toString();

        EntryPoint ep = PayloadParser.parseEntryPoint(entryPoint);
        assertProject(ep.getProjectName());

        Payload payload;
        try {
            payload = payloadManager.createPayload(instanceId, getInitiator(), ep, input);
        } catch (IOException e) {
            throw new WebApplicationException("Error creating a payload", e);
        }

        projectPipeline.process(payload);
        return new StartProcessResponse(instanceId);
    }

    @Override
    @Validate
    public StartProcessResponse start(String projectName, InputStream in) {
        String instanceId = UUID.randomUUID().toString();

<<<<<<< HEAD
            p = addInitiator(p);

            return parseEntryPoint(p, entryPoint);
        } catch (IOException e) {
            throw new ProcessException("Error while parsing a request", e);
        }
    }

    /**
     * Creates a payload from the supplied map of parameters.
     *
     * @param instanceId
     * @param request
     * @return
     */
    private Payload createPayload(String instanceId, String entryPoint, Map<String, Object> request) {
        try {
            Path baseDir = Files.createTempDirectory("request");
            Path workspaceDir = Files.createDirectory(baseDir.resolve("workspace"));
            log.debug("createPayload ['{}'] -> baseDir: {}", instanceId, baseDir);

            Payload p = new Payload(instanceId)
                    .putHeader(Payload.WORKSPACE_DIR, workspaceDir)
                    .mergeValues(Payload.REQUEST_DATA_MAP, request);

            p = addInitiator(p);

            return parseEntryPoint(p, entryPoint);
=======
        Payload payload;
        try {
            payload = payloadManager.createPayload(instanceId, getInitiator(), projectName, in);
>>>>>>> forms
        } catch (IOException e) {
            throw new WebApplicationException("Error creating a payload", e);
        }

<<<<<<< HEAD
    /**
     * Creates a payload from an archive, containing all necessary resources.
     *
     * @param instanceId
     * @param in
     * @return
     */
    private Payload createPayload(String instanceId, InputStream in) {
        try {
            Path baseDir = Files.createTempDirectory("request");
            Path workspaceDir = Files.createDirectory(baseDir.resolve("workspace"));
            log.debug("createPayload ['{}'] -> baseDir: {}", instanceId, baseDir);

            Path archive = baseDir.resolve("_input.zip");
            Files.copy(in, archive);

            Payload p = new Payload(instanceId);
            p = addInitiator(p);
            return p.putHeader(Payload.WORKSPACE_DIR, workspaceDir)
                    .putAttachment(Payload.WORKSPACE_ARCHIVE, archive);
        } catch (IOException e) {
            throw new ProcessException("Error while parsing a request", e);
        }
    }

    /**
     * Creates a payload from an archive, containing all necessary resources and the
     * specified project name.
     *
     * @param instanceId
     * @param in
     * @return
     */
    private Payload createPayload(String instanceId, String projectName, InputStream in) {
        Payload p = createPayload(instanceId, in);
        p = addInitiator(p);
        return p.putHeader(Payload.PROJECT_NAME, projectName);
=======
        projectArchivePipeline.process(payload);
        return new StartProcessResponse(instanceId);
>>>>>>> forms
    }

    @Override
    @Validate
    public ResumeProcessResponse resume(String instanceId, String eventName, Map<String, Object> req) {
        Payload payload;
        try {
            payload = payloadManager.createResumePayload(instanceId, eventName, req);
        } catch (IOException e) {
            throw new WebApplicationException("Error creating a payload", e);
        }

        resumePipeline.process(payload);
        return new ResumeProcessResponse();
    }

    private void assertProject(String projectName) {
        if (!projectDao.exists(projectName)) {
            throw new ValidationErrorsException("Unknown project name: " + projectName);
        }
    }

    @Override
    @Validate
    public ProcessStatusResponse waitForCompletion(String instanceId, long timeout) {
        log.info("waitForCompletion ['{}', {}] -> waiting...", instanceId, timeout);

        long t1 = System.currentTimeMillis();

        ProcessStatusResponse r;
        while (true) {
            r = get(instanceId);
            if (r.getStatus() == ProcessStatus.FINISHED || r.getStatus() == ProcessStatus.FAILED) {
                break;
            }

            if (timeout > 0) {
                long t2 = System.currentTimeMillis();
                if (t2 - t1 >= timeout) {
                    log.warn("waitForCompletion ['{}', {}] -> timeout, last status: {}", instanceId, timeout, r.getStatus());
                    throw new WebApplicationException(Response.status(Status.REQUEST_TIMEOUT).entity(r).build());
                }
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                break;
            }
        }
        return r;
    }

    @Override
    @Validate
    public void kill(String instanceId) {
        ProcessHistoryEntry e = historyDao.get(instanceId);
        if (e == null) {
            log.warn("kill ['{}'] -> not found", instanceId);
            throw new WebApplicationException("Process instance not found", Status.NOT_FOUND);
        }

        ProcessStatus s = e.getStatus();
        if (s == ProcessStatus.SUSPENDED) {
            historyDao.update(instanceId, ProcessStatus.FAILED);
        } else {
            processExecutor.cancel(instanceId);
        }
    }

    @Override
    @Validate
    public ProcessStatusResponse get(String instanceId) {
        ProcessHistoryEntry r = historyDao.get(instanceId);
        if (r == null) {
            log.warn("get ['{}'] -> not found", instanceId);
            throw new WebApplicationException("Process instance not found", Status.NOT_FOUND);
        }

        return new ProcessStatusResponse(r.getCreatedDt(), r.getInitiator(), r.getlastUpdateDt(), r.getStatus(), r.getLogFileName());
    }

    @Override
    @Validate
    public Response downloadAttachment(String instanceId, String attachmentName) {
        if (attachmentName.endsWith("/")) {
            throw new WebApplicationException("Invalid attachment name: " + attachmentName, Status.BAD_REQUEST);
        }

        Path p = attachmentManager.get(instanceId, attachmentName);
        if (p == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok((StreamingOutput) out -> {
            try (InputStream in = Files.newInputStream(p)) {
                IOUtils.copy(in, out);
            }
        }).build();
    }

    private static String getInitiator() {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null || !subject.isAuthenticated()) {
            return null;
        }

        UserEntry u = (UserEntry) subject.getPrincipal();
        return u.getName();
    }

    private static Payload addInitiator(Payload p) {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null || !subject.isAuthenticated()) {
            return null;
        }

        UserEntry u = (UserEntry) subject.getPrincipal();
        return p.putHeader(Payload.INITIATOR, u.getName());
    }
}
