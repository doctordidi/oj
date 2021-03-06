import React, {Component} from 'react'
import {connect} from 'react-redux'
import {Button, Divider, Table} from 'antd'
import NewPaperBox from './new-paper-box'
import {getPapersByPage} from "../../../action/paper-action";
import PreviewPaperModal from "./preview-paper-modal";

class PaperManagementBody extends Component {
  state = {
    isShowNewPaperBox: false,
    isPreviewModalOpen: false,
    isEditModalOpen: false,
    currentPage: 1,
    paper: {quizzes: []}
  }

  componentDidMount = () => {
    this.props.getPapersByPage(this.state.currentPage)
  }

  cancel = () => {
    this.setState({
      isPreviewModalOpen: false,
      isEditModalOpen: false,
      isShowNewPaperBox: false,
      paper: {quizzes: []}
    })
  }

  render() {
    const columns = [
      {
        title: '名称',
        dataIndex: 'title',
        key: 'title'
      }, {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime'
      }, {
        title: '题目个数',
        dataIndex: 'count',
        key: 'count',
        render: (text, record) => {
          return <span>{record.quizzes.length}</span>
        }
      }, {
        title: '操作',
        dataIndex: 'actions',
        key: 'actions',
        render: (text, paper) => {
          return <div>
            <a onClick={() => this.setState({isPreviewModalOpen: true, paper})}>预览</a>
            <Divider type='vertical'/>
            <a onClick={() => this.setState({isEditModalOpen: true, paper})}>编辑</a>
          </div>
        }
      }
    ]
    const {paperPageable} = this.props
    const {totalElements, content} = paperPageable || {}
    const {
      currentPage, isShowNewPaperBox,
      isPreviewModalOpen, paper,
      isEditModalOpen
    } = this.state

    return <div>
      <p><Button
        type="primary"
        onClick={() => this.setState({isShowNewPaperBox: true})}>
        添加试卷
      </Button></p>
      {
        isShowNewPaperBox ?
          <NewPaperBox
            visible={isShowNewPaperBox}
            onCancel={this.cancel}/>
          : ''
      }
      {
        isPreviewModalOpen ?
          <PreviewPaperModal
            visible={isPreviewModalOpen}
            paper={paper}
            onCancel={this.cancel}/>
          : ''
      }
      {
        isEditModalOpen ?
          <NewPaperBox
            visible={isEditModalOpen}
            paper={paper}
            onCancel={this.cancel}/>
          : ''
      }

      <Table
        bordered
        columns={columns}
        dataSource={content}
        rowKey='id'
        onChange={(pagination) => this.getClassCourse(pagination)}
        pagination={{
          defaultCurrent: currentPage,
          total: totalElements
        }}/>


    </div>
  }
}

const mapStateToProps = ({user, paperPageable}) => ({
  user,
  paperPageable
})

const mapDispatchToProps = dispatch => ({
  getPapersByPage: (current) => dispatch(getPapersByPage(current))
})

export default connect(mapStateToProps, mapDispatchToProps)(PaperManagementBody)
