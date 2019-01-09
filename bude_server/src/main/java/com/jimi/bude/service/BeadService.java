package com.jimi.bude.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

import com.jfinal.aop.Enhancer;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jimi.bude.exception.OperationException;
import com.jimi.bude.model.Bead;
import com.jimi.bude.model.Face;
import com.jimi.bude.model.Head;
import com.jimi.bude.model._MappingKit;
import com.jimi.bude.model.vo.BeadVO;
import com.jimi.bude.model.vo.PageUtil;
import com.jimi.bude.service.base.SelectService;
import com.jimi.bude.util.CommonUtil;
import com.jimi.bude.util.ResultUtil;


/**
 * 软件包管理业务层
 * @type BeadService
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年8月28日
 */
public class BeadService extends SelectService {

	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	
	public static final BeadService me = new BeadService();
	
	private final static String SELECT_EXISTENT_HEAD_SQL = "select * from head where name = ?";
	
	private final static String SELECT_EXISTENT_BEAD_SQL = "select * from bead where face_id = ? and first_code = ? and second_code = ? and debug_code = ? and suffix_time = ?";
	
	private final static String BASE_SELECT_BEAD_SQL = "select ";
	
	private final static String FROM_BEAD_SQL = " from bead";
	
	private final static String BASE_WHERE_CLAUSE = " where face_id = ?";
	
	private final static String AND_FIRST_CODE = " and first_code = ?";
	
	private final static String AND_SECOND_CODE = " and second_code = ?";
	
	private final static String AND_DEBUG_CODE = " and debug_code = ?";
	
	private final static String SELECT_BEAD_BY_ID = "select bead.*, face.name as face_name, head.name as head_name from bead inner join face inner join head on (bead.face_id = face.id and face.head_id = head.id) where bead.id = ?";


	/**
	 * 上传文件
	 * @param file 文件
	 * @param fileName 文件名
	 * @param filePath 文件路径
	 * @param headName 项目名
	 * @param faceName 模块名
	 * @param firstCode 主版本号
	 * @param secondCode 次版本号
	 * @param debugCode 修正版本号
	 * @param suffixTime 后缀时间
	 * @param alias 别名
	 * @param updateDescribe 更新描述
	 * @param md5 文件MD5校验码
	 * @return ResultUtil
	 */
	public ResultUtil upload(File file, String fileName, String headName, String faceName, Integer firstCode, Integer secondCode, Integer debugCode, String suffixTime, String alias, String updateDescribe, String md5) {

		String result = "";
		Head head = Head.dao.findFirst(SELECT_EXISTENT_HEAD_SQL, headName);
		if (head == null) {
			result = "head does not exist";
			throw new OperationException(result);
		}
		Integer headId = head.getId();
		Face face = Face.dao.findFirst(FaceService.SELECT_EXISTENT_FACE_SQL, headId, faceName);
		if (face == null) {
			result = "face does not exist";
			throw new OperationException(result);
		}
		Integer faceId = face.getId();
		Bead bead = Bead.dao.findFirst(SELECT_EXISTENT_BEAD_SQL, faceId, firstCode, secondCode, debugCode, suffixTime);
		if (bead != null) {
			result = "bead is already exist";
			throw new OperationException(result);
		}
		String md5Server = getMD5(file).toUpperCase();
		if (!md5.toUpperCase().equals(md5Server)) {
			result = "MD5 Checksum failure";
			throw new OperationException(result);
		}
		new Bead().set("face_id", faceId).set("first_code", firstCode).set("second_code", secondCode).set("debug_code", debugCode).set("suffix_time", suffixTime).set("md5", md5Server).set("alias", alias).set("update_describe", updateDescribe).save();

		File dir = new File(CommonUtil.getFilePath("BEAD", headName, faceName));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File desFile = new File(CommonUtil.getFilePath("BEAD", headName, faceName) + fileName);
		if (desFile.exists()) {
			desFile.delete();
		}
		file.renameTo(desFile);
		return ResultUtil.succeed();
	}


	/**
	 * 更新软件包信息
	 * @param beadId
	 * @param alias
	 * @param updateDescribe
	 * @return
	 */
	public ResultUtil update(Integer beadId, String alias, String updateDescribe) {
		Bead bead = Bead.dao.findById(beadId);
		String result = "";
		if (bead == null) {
			result = "the bead is not exist";
			throw new OperationException(result);
		}

		bead.set("alias", alias).set("update_describe", updateDescribe).update();
		return ResultUtil.succeed();
	}


	/**
	 * 查询软件包信息
	 * @param faceId
	 * @param firstCode
	 * @param secondCode
	 * @param debugCode
	 * @return
	 */
	public ResultUtil select(Integer faceId, Integer firstCode, Integer secondCode, Integer debugCode, String suffixTime, Integer currentPage, Integer pageSize) {
		List<BeadVO> beadVOs = new ArrayList<>();
		Page<Record> pageRecord = new Page<>();
		StringBuilder filter = new StringBuilder();
		PageUtil<BeadVO> pageUtil = new PageUtil<>();
		if (faceId != null) {
			filter.append("face_id = " + faceId);
		}
		if (firstCode != null) {
			if (!filter.toString().equals("")) {
				filter.append(" & ");
			}
			filter.append("first_code = " + firstCode);
		}
		if (secondCode != null) {
			if (!filter.toString().equals("")) {
				filter.append(" & ");
			}
			filter.append("second_code = " + secondCode);
		}
		if (debugCode != null) {
			if (!filter.toString().equals("")) {
				filter.append(" & ");
			}
			filter.append("debug_code = " + debugCode);
		}
		if (suffixTime != null) {
			if (!filter.toString().equals("")) {
				filter.append(" & ");
			}
			filter.append("suffix_time = " + suffixTime);
		}
		pageRecord = selectService.select("bead", currentPage, pageSize, null, null, filter.toString(), null);
		beadVOs = BeadVO.fillList(pageRecord.getList());
		pageUtil.fill(pageRecord, beadVOs);
		return ResultUtil.succeed(pageUtil);
	}


	/**
	 * 查询主版本
	 * @param faceId
	 * @return
	 */
	public ResultUtil selectFirstCode(Integer faceId) {
		List<Bead> beads = Bead.dao.find(BASE_SELECT_BEAD_SQL + "distinct first_code as firstCode" + FROM_BEAD_SQL + BASE_WHERE_CLAUSE, faceId);
		return ResultUtil.succeed(beads);

	}


	/**
	 * 查询次版本
	 * @param faceId
	 * @param firstCode
	 * @return
	 */
	public ResultUtil selectSecondCode(Integer faceId, Integer firstCode) {
		List<Bead> beads = Bead.dao.find(BASE_SELECT_BEAD_SQL + "distinct second_code as secondCode" + FROM_BEAD_SQL + BASE_WHERE_CLAUSE + AND_FIRST_CODE, faceId, firstCode);
		return ResultUtil.succeed(beads);
	}


	/**
	 * 查询修正版本
	 * @param faceId
	 * @param firstCode
	 * @param secondCode
	 * @return
	 */
	public ResultUtil selectDebugCode(Integer faceId, Integer firstCode, Integer secondCode) {
		List<Bead> beads = Bead.dao.find(BASE_SELECT_BEAD_SQL + "distinct debug_code as debugCode" + FROM_BEAD_SQL + BASE_WHERE_CLAUSE + AND_FIRST_CODE + AND_SECOND_CODE, faceId, firstCode, secondCode);
		return ResultUtil.succeed(beads);
	}


	/**
	 * 查询时间后缀
	 * @param faceId
	 * @param firstCode
	 * @param secondCode
	 * @param debugCode
	 * @return
	 */
	public ResultUtil selectSuffixTime(Integer faceId, Integer firstCode, Integer secondCode, Integer debugCode) {
		List<Bead> beads = Bead.dao.find(BASE_SELECT_BEAD_SQL + "distinct suffix_time as suffixTime" + FROM_BEAD_SQL + BASE_WHERE_CLAUSE + AND_FIRST_CODE + AND_SECOND_CODE + AND_DEBUG_CODE, faceId, firstCode, secondCode, debugCode);
		return ResultUtil.succeed(beads);
	}


	/**
	 * 删除软件包
	 * @param beadId
	 * @return
	 */
	public ResultUtil delete(Integer beadId) {
		String result = "";
		Bead bead = Bead.dao.findById(beadId);
		if (bead == null) {
			result = "the database does not have this bead";
			throw new OperationException(result);
		}
		Face face = Face.dao.findById(bead.getFaceId());
		Head head = Head.dao.findById(face.getHeadId());
		String headName = head.getName();
		String faceName = face.getName();
		String firstCode = bead.getFirstCode().toString();
		String secondCode = bead.getSecondCode().toString();
		String debugCode = bead.getDebugCode().toString();
		String suffixTime = bead.getSuffixTime().toString();
		String fileName = CommonUtil.getFileName(headName, faceName, firstCode, secondCode, debugCode, suffixTime);
		String filePath = CommonUtil.getFilePath("BEAD", headName, faceName);
		File file = CommonUtil.findFile(filePath, fileName);
		try {
			Bead.dao.deleteById(beadId);
		} catch (Exception e) {
			result = "the bead has been referenced";
			throw new OperationException(result);
		}
		if (file == null) {
			result = "the bead is not exist";
			throw new OperationException(result);
		}
		boolean deleteFlag = file.delete();
		if (!deleteFlag) {
			result = "the bead delete fail, file occupancy";
			throw new OperationException(result);
		}
		return ResultUtil.succeed();
	}


	public Bead selectById(Integer beadId) {
		Bead bead = Bead.dao.findFirst(SELECT_BEAD_BY_ID, beadId);
		return bead;
	}


	/**
	 * 计算文件MD5
	 * @param file
	 * @return
	 */
	public static String getMD5(File file) {
		FileInputStream fileInputStream = null;
		try {
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			fileInputStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				MD5.update(buffer, 0, length);
			}
			return new String(Hex.encodeHex(MD5.digest()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


	/**
	 * 测试方法
	 * @param args
	 */
	public static void main(String[] args) {
		PropKit.use("config.properties");
		DruidPlugin dp = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"), PropKit.get("driver"));
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		arp.setDialect(new MysqlDialect()); // 用什么数据库，就设置什么Dialect
		arp.setShowSql(true);
		_MappingKit.mapping(arp);
		dp.start();
		arp.start();
	}

}
