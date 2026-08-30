const AnimationUtils = {
    lerp: function (start, end, t) {
        return start + (end - start) * t;
    },

    cubicBezier: function (x1, y1, x2, y2) {
        function bezierComponent(t, p1, p2) {
            const u = 1 - t;
            return 3 * u * u * t * p1 + 3 * u * t * t * p2 + t * t * t;
        }
        function bezierComponentDerivative(t, p1, p2) {
            const u = 1 - t;
            return 3 * u * u * p1 + 6 * u * t * (p2 - p1) + 3 * t * t * (1 - p2);
        }
        return function (t) {
            if (t <= 0) return 0;
            if (t >= 1) return 1;
            var guess = t;
            for (var i = 0; i < 8; i++) {
                var x = bezierComponent(guess, x1, x2) - t;
                var dx = bezierComponentDerivative(guess, x1, x2);
                if (Math.abs(dx) < 1e-6) break;
                guess -= x / dx;
                if (guess < 0) guess = 0;
                if (guess > 1) guess = 1;
            }
            return bezierComponent(guess, y1, y2);
        };
    }
};

const AnimationManager = {
    ScrollState: {
        BEGIN: 0,
        SCROLLING: 1,
        END: 2
    },

    marqueeText: function(state, animId, text, options) {
        if (!state.animations) state.animations = {};
        const o = options || {};
        const scale = o.scale || 0.5;
        const waitTime = o.waitTime || 5;
        const speed = o.speed || 8;
        const marginLeft = o.marginLeft != null ? o.marginLeft : 2;
        const maxWidth = o.maxWidth;
        const noScrollThreshold = o.noScrollThreshold != null ? o.noScrollThreshold : maxWidth;
        const loopOverlap = o.loopOverlap != null ? o.loopOverlap : (116 * scale);

        let anim = state.animations[animId];
        if (!anim || anim.key !== text) {
            const chars = [];
            let cursor = 0;
            text.split("").forEach(function(ch) {
                const w = RenderUtils.getCharWidth(ch, scale) + scale;
                chars.push({ text: ch, x: cursor, width: w });
                cursor += w;
            });
            anim = state.animations[animId] = {
                key: text, chars: chars, totalWidth: cursor,
                offset: -marginLeft, phase: "wait", timeInState: 0
            };
        }

        if (anim.totalWidth <= noScrollThreshold) {
            return { chars: anim.chars, totalWidth: anim.totalWidth, offset: 0, loopWidth: 0, needsScroll: false };
        }

        const loopGap = Math.max(0, maxWidth - loopOverlap);
        const loopWidth = anim.totalWidth + loopGap;
        const delta = Timing.delta();
        anim.timeInState += delta;

        if (anim.phase === "wait") {
            if (anim.timeInState > waitTime) { anim.phase = "scroll"; anim.timeInState = 0; }
        } else if (anim.phase === "scroll") {
            anim.offset += speed * delta;
            if (anim.offset >= loopWidth - marginLeft) {
                anim.offset = -marginLeft;
                anim.phase = "wait";
                anim.timeInState = 0;
            }
        }

        return { chars: anim.chars, totalWidth: anim.totalWidth, offset: anim.offset, loopWidth: loopWidth, needsScroll: true };
    },

    scrollVertical: function(state, animId, totalItems, options) {
        if (!state.animations) state.animations = {};

        const maxVisible = options.maxVisible || 14;
        const waitTime = options.waitTime || 7;
        const speed = options.speed || 2.4;
        const itemHeight = options.itemHeight || 5.5;

        if (!state.animations[animId] || state.animations[animId].itemCount !== totalItems) {
            state.animations[animId] = {
                itemCount: totalItems,
                state: this.ScrollState.BEGIN,
                timeInState: 0,
                currentIndex: 0,
                pixelOffset: 0
            };
        }

        let anim = state.animations[animId];

        if (totalItems <= maxVisible) {
            return { currentIndex: 0, offset: 0 };
        }

        const delta = Timing.delta();
        anim.timeInState += delta;

        switch (anim.state) {
            case this.ScrollState.BEGIN:
                if (anim.timeInState > waitTime) {
                    anim.state = this.ScrollState.SCROLLING;
                    anim.timeInState = 0;
                }
                break;

            case this.ScrollState.SCROLLING:
                anim.pixelOffset += speed * delta;

                while (anim.pixelOffset >= itemHeight) {
                    anim.pixelOffset -= itemHeight;
                    anim.currentIndex++;

                    if (anim.currentIndex >= totalItems - maxVisible) {
                        anim.state = this.ScrollState.END;
                        anim.timeInState = 0;
                        anim.pixelOffset = 0;
                        break;
                    }
                }
                break;

            case this.ScrollState.END:
                if (anim.timeInState > waitTime) {
                    anim.state = this.ScrollState.BEGIN;
                    anim.timeInState = 0;
                    anim.pixelOffset = 0;
                    anim.currentIndex = 0;
                }
                break;
        }

        return {
            currentIndex: anim.currentIndex,
            offset: anim.pixelOffset
        };
    }
};
