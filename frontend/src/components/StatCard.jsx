import { Box, Card, CardContent, Typography } from '@mui/material'
import { alpha } from '@mui/material/styles'

/** A headline metric: accent icon, label, big value, and a caption. */
export default function StatCard({ label, value, icon, caption }) {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent sx={{ p: 2.5 }}>
        <Box
          sx={{
            width: 40,
            height: 40,
            borderRadius: 2,
            display: 'grid',
            placeItems: 'center',
            bgcolor: (t) => alpha(t.palette.primary.main, 0.1),
            color: 'primary.main',
            mb: 1.5,
          }}
        >
          {icon}
        </Box>
        <Typography variant="overline" color="text.secondary" display="block">
          {label}
        </Typography>
        <Typography variant="h4" component="div" sx={{ mt: 0.25 }} noWrap>
          {value}
        </Typography>
        {caption && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
            {caption}
          </Typography>
        )}
      </CardContent>
    </Card>
  )
}
