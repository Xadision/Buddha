package com.jimi.bude.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.bude.entity.ControllType;
import com.jimi.bude.entity.Infomation;
import com.jimi.bude.exception.OperationException;
import com.jimi.bude.model.Bead;
import com.jimi.bude.model.Face;
import com.jimi.bude.model.Head;
import com.jimi.bude.model.vo.FaceVO;
import com.jimi.bude.model.vo.FingerAndBeadVO;
import com.jimi.bude.model.vo.PageUtil;
import com.jimi.bude.pack.UpdatePackage;
import com.jimi.bude.service.base.SelectService;
import com.jimi.bude.socket.ArmClient;
import com.jimi.bude.socket.Bude;
import com.jimi.bude.util.CommonUtil;
import com.jimi.bude.util.ResultUtil;

import cc.darhao.dautils.api.StringUtil;

/**
 * 模块业务逻辑层
 * @type FaceService
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年10月8日
 */
public class FaceService extends SelectService {

	public final static String SELECT_EXISTENT_FACE_SQL = "select * from face where head_id = ? and name = ?";
	public final static String SELECT_FACE_BY_HEAD_SQL = "select * from face where head_id = ?";
	public final static String SELECT_BEAD_BY_FACE = "select * from bead where face_id = ?";
	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	public final static FaceService me = new FaceService();

	/**
	 * 添加模块
	 * @param headId
	 * @param faceName
	 * @return
	 */
	public ResultUtil add(Integer headId, String faceName) {
		String result = "";
		Head head = Head.dao.findById(headId);
		if (head == null) {
			result = "head is not exist";
			throw new OperationException(result);
		}
		Face face = Face.dao.findFirst(SELECT_EXISTENT_FACE_SQL, headId, faceName);
		if (face != null) {
			result = "face already exist";
			throw new OperationException(result);
		}
		face = new Face();
		face.setHeadId(headId);
		face.setName(faceName);
		face.save();
		return ResultUtil.succeed();
	}

	/**
	 * 更新模块信息
	 * @param faceId
	 * @param faceName
	 * @param headId
	 * @return
	 */
	public synchronized ResultUtil update(Integer faceId, String faceName, Integer headId) {
		String result = "";
		Face face = Face.dao.findFirst(SELECT_EXISTENT_FACE_SQL, headId, faceName);
		if (face != null && face.getId() != faceId) {
			result = "face already exist, cannot repeat";
			throw new OperationException(result);
		}
		face = Face.dao.findById(faceId);
		if (face == null) {
			result = "faceId is not exist";
			throw new OperationException(result);
		}
		Head head = Head.dao.findById(headId);
		if (head == null) {
			result = "headId is not exist";
			throw new OperationException(result);
		}
		File[] files = CommonUtil.findFaceFiles(CommonUtil.getFilePath("BEAD", head.getName(), face.getName()));
		if (files != null && files.length > 0) {
			for (File file : files) {
				String fileName = file.getName();
				String font = fileName.substring(0, fileName.indexOf("-")+1);
				String tail = fileName.substring(fileName.indexOf("_"));
				String newFileName = font + faceName + tail;
				CommonUtil.rename(CommonUtil.getFilePath("BEAD", head.getName(), face.getName()), fileName, newFileName);
			}
		}
		CommonUtil.rename(CommonUtil.getFilePath("BEAD", head.getName()), face.getName(), faceName);
		face.setHeadId(headId);
		face.setName(faceName);
		face.update();
		return ResultUtil.succeed();
	}

	/**
	 * 根据项目ID查询模块
	 * @param headId
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public ResultUtil select(Integer headId, Integer currentPage, Integer pageSize) {
		PageUtil<FaceVO> pageUtil = new PageUtil<>();
		Page<Record> pageRecord = new Page<>();
		List<FaceVO> list = new ArrayList<FaceVO>();
		String filter = "head_Id = " + headId;
		String tables = "face";
		pageRecord = selectService.select(tables, currentPage, pageSize, null, null, filter, null);
		list = FaceVO.fillList(pageRecord.getList());
		pageUtil.fill(pageRecord, list);
		return ResultUtil.succeed(pageUtil);

	}

	/**
	 * 查询应用某模块的设备端信息
	 * @param faceId
	 * @param firstCode
	 * @param secondCode
	 * @param debugCode
	 * @param suffixTime
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public ResultUtil selectFinger(Integer faceId, Integer firstCode, Integer secondCode, Integer debugCode, String suffixTime, Integer currentPage, Integer pageSize) {
		List<FingerAndBeadVO> list = new ArrayList<>();
		Page<Record> pageRecord = new Page<>();
		String[] table = {"bead", "finger", "head", "face"};
		String[] refers = {"finger.bead_id = bead.id", "bead.face_id = face.id", "face.head_id = head.id"};
		String[] discard = {"face.id", "head.id", "face.head_id"};
		String filter = "";
		PageUtil<FingerAndBeadVO> pageUtil = new PageUtil<>();
		filter += "face_id = " + faceId;
		if (firstCode == null) {
			pageRecord = selectService.select(table, refers, currentPage, pageSize, null, null, filter, discard);
			list = FingerAndBeadVO.fillList(pageRecord.getList());
			pageUtil.fill(pageRecord, list);
			return ResultUtil.succeed(pageUtil);
		}
		filter += "&first_code = " + firstCode;
		if (secondCode == null) {
			pageRecord = selectService.select(table, refers, currentPage, pageSize, null, null, filter, discard);
			list = FingerAndBeadVO.fillList(pageRecord.getList());
			pageUtil.fill(pageRecord, list);
			return ResultUtil.succeed(pageUtil);
		}
		filter += "&second_code = " + secondCode;
		if (debugCode == null) {
			pageRecord = selectService.select(table, refers, currentPage, pageSize, null, null, filter, discard);
			list = FingerAndBeadVO.fillList(pageRecord.getList());
			pageUtil.fill(pageRecord, list);
			return ResultUtil.succeed(pageUtil);
		}
		filter += "&debug_code = " + debugCode;
		if (suffixTime == null) {
			pageRecord = selectService.select(table, refers, currentPage, pageSize, null, null, filter, discard);
			list = FingerAndBeadVO.fillList(pageRecord.getList());
			pageUtil.fill(pageRecord, list);
			return ResultUtil.succeed(pageUtil);
		}
		filter += "&suffix_time = " + suffixTime;
		pageRecord = selectService.select(table, refers, currentPage, pageSize, null, null, filter, discard);
		list = FingerAndBeadVO.fillList(pageRecord.getList());
		pageUtil.fill(pageRecord, list);
		return ResultUtil.succeed(pageUtil);
	}

	/**
	 * 删除模块
	 * @param faceId
	 * @return
	 */
	public ResultUtil delete(Integer faceId) {
		String result = "";
		Face face = Face.dao.findById(faceId);
		if (face == null) {
			result = "faceId is not exist";
			throw new OperationException(result);
		}
		try {
			face.delete();
		} catch (Exception e) {
			result = "the face has been referenced";
			throw new OperationException(result);
		}
		return ResultUtil.succeed();
	}

	/**
	 * 发布软件包
	 * @param armName
	 * @param fingerName
	 * @param beadId
	 * @return
	 */
	public ResultUtil publish(String armName, String fingerName, Integer beadId) {
		Bead bead = BeadService.me.selectById(beadId);
		String result = "";
		short controllId = ArmClient.genControllId();
		if (bead != null) {
			ArmClient armClient = Bude.armClientMap.get(armName);
			if (armClient != null) {
				Infomation info = new Infomation();
				info.setArmName(armName);
				info.setFingerName(fingerName);
				info.setType(ControllType.UPDATE);
				info.setBeadId(beadId);
				UpdatePackage update = new UpdatePackage();
				Bude.controllMap.put(controllId, info);
				update.setControllId(controllId);
				update.setFingerName(fingerName);
				System.out.println(bead.getStr("head_name") + "  aa  " + bead.getStr("face_name"));
				update.setHeadName(bead.getStr("head_name"));
				update.setFaceName(bead.getStr("face_name"));
				update.setFirstCode(bead.getFirstCode());
				update.setSecondCode(bead.getSecondCode());
				update.setDebugCode(bead.getDebugCode());
				update.setSuffixTime(bead.getSuffixTime());
				update.setMd5(StringUtil.stretch(bead.getMd5(), 2));
				CountDownLatch countDownLatch = new CountDownLatch(1);
				Bude.countDownMap.put(controllId, countDownLatch);
				armClient.sendUpdatePackage(update);
				// 定时器待填充
				try {
					countDownLatch.await(5000, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (countDownLatch.getCount() != 0) {
					result = "the arm recieve update package fail";
				} else {
					result = "the arm recieve update package succeed";
				}
			} else {
				result = "the arm is not online";
			}
		} else {
			Bude.countDownMap.remove(controllId);
			result = "the bead is not exist";
			throw new OperationException(result);
		}
		Bude.countDownMap.remove(controllId);
		return ResultUtil.succeed(result);
	}
}
