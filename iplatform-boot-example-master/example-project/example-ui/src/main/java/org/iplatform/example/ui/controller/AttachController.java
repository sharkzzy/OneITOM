package org.iplatform.example.ui.controller;

import java.security.Principal;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.iplatform.example.ui.feign.AttachClient;
import org.iplatform.example.util.domain.Attach;
import org.iplatform.example.util.domain.AttachQuery;
import org.iplatform.example.util.domain.BootstrapTable;
import org.iplatform.microservices.core.dfss.DFSSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/attach")
public class AttachController {

	private static final Logger LOG = LoggerFactory.getLogger(AttachController.class);

	@Autowired
	private DFSSClient dfssClient;

	@Autowired
	private AttachClient attachClient;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Attach> upload(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request,
			Principal principal) {

		Attach attach = null;
		try {
			LOG.info("文件名称={},文件大小={}", file.getOriginalFilename(), file.getSize());
			String fileId = dfssClient.add(file.getName(), file.getInputStream());
			if (fileId != null) {
				attach = new Attach();
				attach.setFileId(fileId);
				attach.setFileName(file.getOriginalFilename());
				attach.setFileSize(file.getSize());
				attach.setUploadMan(principal.getName());
			}
		} catch (Exception e) {
			LOG.error("文件上传失败", e);
		}
		return new ResponseEntity<>(attach, HttpStatus.OK);

	}

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public ResponseEntity<BootstrapTable> attachList(@RequestBody AttachQuery query) {

		return attachClient.list(query);

	}

	@RequestMapping(value = "/", method = RequestMethod.PUT)
	public ResponseEntity<Attach> addAttach(@RequestBody Attach attach) {

		return attachClient.addAttach(attach);

	}

	@RequestMapping(value = "/{fileId}", method = RequestMethod.PUT)
	public ResponseEntity<Attach> updateAttach(@RequestBody Attach attach) {

		return attachClient.updateAttach(attach);

	}

	@RequestMapping(value = "/{fileId}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> deleteAttach(@PathVariable(value = "fileId") String fileId) {

		return attachClient.deleteAttach(fileId);

	}

	@RequestMapping(value = "/{fileId}", method = RequestMethod.GET)
	public ResponseEntity<Attach> getAttach(@PathVariable(value = "fileId") String fileId) {

		return attachClient.getAttach(fileId);

	}

}
