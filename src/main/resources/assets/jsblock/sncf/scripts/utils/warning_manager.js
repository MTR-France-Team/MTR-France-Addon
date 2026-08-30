const WarningManager = function () {
    this.IMAGES_AVAILABLE = 4;
    this.STATIC_IMAGE_DURATION = 6000;
    this.imageIndex = 1;
    this.lastImageUpdate = null;
}


WarningManager.prototype = {
    update (ctx, pids) {
        if (this.lastImageUpdate == null) {
            this.lastImageUpdate = Date.now();
        }

        this.showStaticImage(ctx, pids);
    },
    showStaticImage (ctx, pids) {
        this.drawTexture(ctx, pids, "jsblock:sncf/images/warning_frames/" + this.formatFrameNumber(this.imageIndex) + ".png");

        let now = Date.now();

        if (now >= this.lastImageUpdate + this.STATIC_IMAGE_DURATION) {
            this.lastImageUpdate = now;
            if(this.imageIndex == this.IMAGES_AVAILABLE) this.imageIndex = 1;
            else this.imageIndex++;
        }
    },
    drawTexture (ctx, pids, path) {
        Texture.create()
        .texture(path)
        .size(pids.width, pids.height)
        .pos(0, 0)
        .draw(ctx);
    },
    formatFrameNumber (frameIndex) {
        return String(frameIndex).padStart(3, "0");
    }
}