Component({
  properties: {
    item: { type: Object, value: {} }
  },
  methods: {
    onTap() { this.triggerEvent('cardtap', { video: this.data.item }) }
  }
})
