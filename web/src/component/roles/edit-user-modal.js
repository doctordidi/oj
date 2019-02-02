import React from 'react'
import {Row, Button, Col, Form, Input, Modal} from 'antd'
import {message} from "antd/lib/index";
import moment from 'moment'

const formItemLayout = {
  labelCol: {
    xs: {span: 24},
    sm: {span: 4},
  },
  wrapperCol: {
    xs: {span: 24},
    sm: {span: 16},
  },
}

class EditUserModal extends React.Component {

  state = {
    id: -1
  }

  handleSubmit = (e) => {
    e.preventDefault()
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        values['id'] = this.state.id
        this.props.putUser(values, () => {
          message.success('更新成功')
          this.props.closeModal()
        })
      }
    })
  }

  componentWillReceiveProps (nextProps) {
    const {user, form} = nextProps
    if (user === this.props.user ) {
      return false
    }
    const {id, username, createTime, available, name, phone, email} = user
    form.setFieldsValue({username, createTime: moment(createTime), available, name, phone, email})
    this.setState({id})
  }

  render() {
    const {isEditModalOpen, closeModal, form} = this.props
    const {getFieldDecorator} = form

    return <div>
      <Modal
        title='更新用户信息'
        visible={isEditModalOpen}
        footer={null}
        onCancel={() => closeModal()}
      >
        <Form onSubmit={this.handleSubmit}>
        <Form.Item
            {...formItemLayout}
            label="用户名"
          >
            {getFieldDecorator('username', {
              rules: [{
                required: true, message: '请输入用户名',
              }],
            })(
              <Input/>
            )}
          </Form.Item>
          <Form.Item
            {...formItemLayout}
            label="真实姓名"
          >
            {getFieldDecorator('name', {
              rules: [{
                required: true, message: '请输入用户真实姓名',
              }],
            })(
              <Input/>
            )}
          </Form.Item>
          <Form.Item
            {...formItemLayout}
            label="手机号"
          >
            {getFieldDecorator('phone', {
              rules: [{
                required: true, message: '请输入手机号',
              }],
            })(
              <Input/>
            )}
          </Form.Item>
          <Form.Item
            {...formItemLayout}
            label="邮箱"
          >
            {getFieldDecorator('email', {
              rules: [{
                required: true, message: '请输入邮箱',
              }],
            })(
              <Input/>
            )}
          </Form.Item>
          
          <Row type='flex' align='center'>
            <Col>
              <Button type="primary"
                      htmlType="submit">确定</Button>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  }
}

const EditUserModalForm = Form.create()(EditUserModal)

export default EditUserModalForm
