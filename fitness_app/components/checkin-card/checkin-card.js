Component({
  properties: {
    item: { type: Object, value: {} }
  },
  methods: {
    onTap() { this.triggerEvent('tap', { item: this.data.item }) },
    onUserTap() { this.triggerEvent('usertap', { userId: this.data.item.userId }) },
    onLike() { this.triggerEvent('like', { item: this.data.item }) },
    onComment() { this.triggerEvent('comment', { item: this.data.item }) }
  }
})
