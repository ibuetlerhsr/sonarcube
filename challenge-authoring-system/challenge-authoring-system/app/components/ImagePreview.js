import React, {Component, PropTypes} from 'react';

export default class ImagePreview extends Component {
    constructor(props) {
        super(props);
        this.state = {file: '', imagePreviewUrl: ''};
    }

    /* handleImageChange(e) {
        e.preventDefault();

        const reader = new FileReader();
        const file = e.target.files[0];

        reader.onloadend = () => {
            this.setState({
                file: file,
                imagePreviewUrl: reader.result
            });
        }

        reader.readAsDataURL(file)
    }*/

    render = () => {
        // let {imagePreviewUrl} = this.state;
        const {uploadedFile} = this.props;
        let imagePreview = null;
        if (uploadedFile) {
            imagePreview = (<img src={uploadedFile} />);
        } else {
            imagePreview = (<div className="previewText">Please select an Image for Preview</div>);
        }

        return (
            <div className="previewComponent">
                <div className="imgPreview">
                    {imagePreview}
                </div>
            </div>
        );
    }
}

ImagePreview.propTypes = {
    uploadedFile: PropTypes.string.isRequired,
    errorMessage: PropTypes.string
};
